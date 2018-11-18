package com.github.hashicorp.packer.engine.types

import com.fasterxml.jackson.annotation.JsonCreator
import com.github.hashicorp.packer.engine.exceptions.InvalidRawValueClass
import com.github.hashicorp.packer.engine.exceptions.ObjectAlreadyInterpolatedWithFixedContext
import com.github.hashicorp.packer.template.Context
import com.google.common.base.Supplier
import com.google.common.base.Suppliers
import com.google.common.reflect.TypeToken
import groovy.transform.CompileStatic
import groovy.transform.CompileDynamic
import groovy.transform.EqualsAndHashCode
import groovy.transform.AutoClone
import groovy.transform.AutoCloneStyle
import com.fasterxml.jackson.annotation.JsonValue
import groovy.transform.PackageScope
import sun.plugin.dom.exception.InvalidStateException
import java.util.concurrent.ConcurrentHashMap
import java.util.function.Function

// @AutoClone(style = AutoCloneStyle.SIMPLE) TODO
// equals is required for Gradle up-to-date checking
// @AutoExternalize(excludes = ['rawValue']) // TODO: Groovy 2.5.0
@CompileStatic
// Serializable and Externalizable are required for Gradle up-to-date checking
interface InterpolableValue<
  Source,
  Target extends Serializable,
  ThisClass extends InterpolableValue<Source, Target, ThisClass>
> extends InterpolableObject<ThisClass>, Supplier<Target> {
  @JsonValue
  Source getRawValue()

  void setRawValue(Source rawValue)

  private abstract static class AbstractInterpolableValue<
    Source,
    Target extends Serializable,
    ThisClass extends InterpolableValue<Source, Target, ThisClass>,
    InterpolableClass extends Interpolable<Source, Target, ThisClass, InterpolableClass, InitializedClass, AlreadyInterpolatedClass> & ThisClass,
    InitializedClass extends Initialized<Source, Target, ThisClass, InterpolableClass, InitializedClass, AlreadyInterpolatedClass> & ThisClass,
    AlreadyInterpolatedClass extends AlreadyInterpolated<Source, Target, ThisClass, InterpolableClass, InitializedClass, AlreadyInterpolatedClass> & ThisClass
  > implements InterpolableValue<Source, Target, ThisClass> {
    @Override
    final ThisClass interpolate(Context context) {
      throw new UnsupportedOperationException('Value objects should be interpolated with deledate object')
    }
  }

  ThisClass interpolateValue(Context context, InterpolableObject delegate)

  abstract static class Interpolable<
    Source,
    Target extends Serializable,
    ThisClass extends InterpolableValue<Source, Target, ThisClass>,
    InterpolableClass extends Interpolable<Source, Target, ThisClass, InterpolableClass, InitializedClass, AlreadyInterpolatedClass> & ThisClass,
    InitializedClass extends Initialized<Source, Target, ThisClass, InterpolableClass, InitializedClass, AlreadyInterpolatedClass> & ThisClass,
    AlreadyInterpolatedClass extends AlreadyInterpolated<Source, Target, ThisClass, InterpolableClass, InitializedClass, AlreadyInterpolatedClass> & ThisClass
    > extends AbstractInterpolableValue<Source, Target, ThisClass, InterpolableClass, InitializedClass, AlreadyInterpolatedClass> {
    @SuppressWarnings('UnstableApiUsage')
    static final Class<InitializedClass> INITIALIZED_CLASS = (Class<InitializedClass>)new TypeToken<InitializedClass>(this.class) { }.rawType

    private volatile /* TODO */ Source rawValue

    protected Interpolable() {
      this.@rawValue = null
    }

    @JsonCreator
    /* protected TODO */ Interpolable(Source rawValue) {
      this.@rawValue = rawValue
    }

    @Override
    final Source getRawValue() {
      this.@rawValue
    }

    @Override
    void setRawValue(Source rawValue) {
      this.@rawValue = rawValue
    }

    /*
     * CAVEAT:
     * We use dynamic compiling to run
     * overloaded version of doInterpolatePrimitive
     * depending on rawValue actual type
     */
    @CompileDynamic
    protected Target doInterpolatePrimitive(Context context) {
      doInterpolatePrimitive context, rawValue
    }

    protected static /* TOTEST */ Target doInterpolatePrimitive(Context context, Object rawValue) {
      throw new InvalidRawValueClass(rawValue)
    }

    @Override
    ThisClass interpolateValue(Context context, InterpolableObject delegate) {
      throw new InvalidStateException('Object is not initialized yet')
    }

    @Override
    final Target get() {
      throw new InvalidStateException('Value is not interpolated yet')
    }

    private final InitializedClass init(Supplier<Target> defaultValueSupplier, Closure<Boolean> ignoreIf, Closure<Target> postProcess) {
      INITIALIZED_CLASS.newInstance(this, defaultValueSupplier, ignoreIf, postProcess)
    }
  }

  abstract static class Initialized<
    Source,
    Target extends Serializable,
    ThisClass extends InterpolableValue<Source, Target, ThisClass>,
    InterpolableClass extends Interpolable<Source, Target, ThisClass, InterpolableClass, InitializedClass, AlreadyInterpolatedClass> & ThisClass,
    InitializedClass extends Initialized<Source, Target, ThisClass, InterpolableClass, InitializedClass, AlreadyInterpolatedClass> & ThisClass,
    AlreadyInterpolatedClass extends AlreadyInterpolated<Source, Target, ThisClass, InterpolableClass, InitializedClass, AlreadyInterpolatedClass> & ThisClass
    > extends AbstractInterpolableValue<Source, Target, ThisClass, InterpolableClass, InitializedClass, AlreadyInterpolatedClass> {
    @SuppressWarnings('UnstableApiUsage')
    static final Class<AlreadyInterpolatedClass> ALREADY_INTERPOLATED_CLASS = (Class<AlreadyInterpolatedClass>)new TypeToken<AlreadyInterpolatedClass>(this.class) { }.rawType

    private final InterpolableClass interpolable

    private final ConcurrentHashMap<Context, AlreadyInterpolatedClass> interpolatedValues = [:]

    private final Supplier<Target> defaultValueSupplier

    private final Closure<Boolean> ignoreIf

    private final Closure<Target> postProcess

    protected Initialized(InterpolableClass interpolable, Supplier<Target> defaultValueSupplier, Closure<Boolean> ignoreIf, Closure<Target> postProcess) {
      this.@interpolable = interpolable
      this.@defaultValueSupplier = defaultValueSupplier
      this.@ignoreIf = (Closure<Boolean>)ignoreIf.clone()
      this.@ignoreIf.resolveStrategy = Closure.DELEGATE_ONLY
      this.@postProcess = (Closure<Target>)postProcess.clone()
      this.@postProcess.resolveStrategy = Closure.TO_SELF
    }

    @JsonValue
    @Override
    final Source getRawValue() {
      this.interpolable.rawValue
    }

    @Override
    void setRawValue(Source rawValue) {
      this.interpolable.rawValue = rawValue
    }

    @Override
    ThisClass interpolateValue(Context context, InterpolableObject delegate) {
      interpolatedValues.computeIfAbsent(context, new Function<Context, AlreadyInterpolatedClass>() {
        @Override
        AlreadyInterpolatedClass apply(Context aContext) {
          Boolean ignore
          if (ignoreIf != null) {
            Closure<Boolean> ignoreIf = (Closure<Boolean>)ignoreIf.clone()
            ignoreIf.delegate = delegate
            ignore = ignoreIf.call()
          } else {
            ignore = null
          }
          Supplier<Target> interpolatedValueSupplier = null
          if (ignore != Boolean.TRUE) {
            if (rawValue != null) {
              interpolatedValueSupplier = Suppliers.memoize(new Supplier<Target>() {
                @Override
                Target get() {
                  Target interpolatedValue = interpolable.doInterpolatePrimitive(context)
                  if (interpolatedValue != null) {
                    if (postProcess) {
                      interpolatedValue = postProcess.call(interpolatedValue)
                    }
                  } else {
                    interpolatedValue = defaultValueSupplier?.get()
                  }
                  interpolatedValue
                }
              })
            } else {
              interpolatedValueSupplier = defaultValueSupplier
            }
          }
          AlreadyInterpolatedClass result = ALREADY_INTERPOLATED_CLASS.newInstance()
          result.interpolatedValue = interpolatedValueSupplier
          result
        }
      })

      // postProcess.delegate =
      // postProcess.memoize()
      // postProcess.resolveStrategy = Closure.DELEGATE_ONLY
    }

    @Override
    final Target get() {
      throw new InvalidStateException('Value is not interpolated yet')
    }
  }

  @AutoClone(style = AutoCloneStyle.SIMPLE) // TODO
  @EqualsAndHashCode(includes = ['interpolatedValue'])
  abstract static class AlreadyInterpolated<
    Source,
    Target extends Serializable,
    ThisClass extends InterpolableValue<Source, Target, ThisClass>,
    InterpolableClass extends Interpolable<Source, Target, ThisClass, InterpolableClass, InitializedClass, AlreadyInterpolatedClass> & ThisClass,
    InitializedClass extends Initialized<Source, Target, ThisClass, InterpolableClass, InitializedClass, AlreadyInterpolatedClass> & ThisClass,
    AlreadyInterpolatedClass extends AlreadyInterpolated<Source, Target, ThisClass, InterpolableClass, InitializedClass, AlreadyInterpolatedClass> & ThisClass
  > extends AbstractInterpolableValue<Source, Target, ThisClass, InterpolableClass, InitializedClass, AlreadyInterpolatedClass> implements Externalizable {
    @JsonValue
    @Override
    final Source getRawValue() {
      throw new ObjectAlreadyInterpolatedWithFixedContext()
    }

    @Override
    void setRawValue(Source rawValue) {
      throw new ObjectAlreadyInterpolatedWithFixedContext()
    }

    private Object interpolatedValue
    // ThisClass constructor is required for Externalizable and AutoClone
    protected AlreadyInterpolated() { }

    @SuppressWarnings('unused') // IDEA bug
    private static final long serialVersionUID = 7881876550613522317L

    /**
     * @serialData interpolatedValue
     */
    @Override
    void writeExternal(ObjectOutput out) throws IOException {
      out.writeObject(get())
    }

    @Override
    void readExternal(ObjectInput oin) throws IOException, ClassNotFoundException {
      interpolatedValue = (Target)oin.readObject()
    }

    @Override
    ThisClass interpolateValue(Context context, InterpolableObject delegate) {
      throw new ObjectAlreadyInterpolatedWithFixedContext()
    }
    /**
     * @serial Interpolated value
     */
    @Override
    final Target get() {
      Supplier.isInstance(interpolatedValue) ? ((Supplier<Target>)interpolatedValue).get() : (Target)interpolatedValue
    }
  }

  @PackageScope
  static class Utils {
    static boolean requiresInitialization(InterpolableValue interpolableValue) {
      interpolableValue == null || Interpolable.isInstance(interpolableValue)
    }

    // ThisClass is used to create instances with default values
    protected static final <
      Source,
      Target extends Serializable,
      ThisClass extends InterpolableValue<Source, Target, ThisClass>,
      InterpolableClass extends Interpolable<Source, Target, ThisClass, InterpolableClass, InitializedClass, AlreadyInterpolatedClass> & ThisClass,
      InitializedClass extends Initialized<Source, Target, ThisClass, InterpolableClass, InitializedClass, AlreadyInterpolatedClass> & ThisClass,
      AlreadyInterpolatedClass extends AlreadyInterpolated<Source, Target, ThisClass, InterpolableClass, InitializedClass, AlreadyInterpolatedClass> & ThisClass
    > ThisClass initWithDefault(Class<InterpolableClass> interpolableClass, ThisClass interpolableValue, Supplier<Target> defaultValueSupplier, Closure<Boolean> ignoreIf, Closure<Target> postProcess) {
      InterpolableClass interpolable = (InterpolableClass)interpolableValue ?: interpolableClass.newInstance()
      interpolable.init(defaultValueSupplier, ignoreIf, postProcess)
    }



    // ThisClass is used to create instances with default values
    protected static final <
      Source,
      Target extends Serializable,
      ThisClass extends InterpolableValue<Source, Target, ThisClass>,
      InterpolableClass extends Interpolable<Source, Target, ThisClass, InterpolableClass, InitializedClass, AlreadyInterpolatedClass> & ThisClass,
      InitializedClass extends Initialized<Source, Target, ThisClass, InterpolableClass, InitializedClass, AlreadyInterpolatedClass> & ThisClass,
      AlreadyInterpolatedClass extends AlreadyInterpolated<Source, Target, ThisClass, InterpolableClass, InitializedClass, AlreadyInterpolatedClass> & ThisClass
    > ThisClass initWithDefault(Class<InterpolableClass> interpolableClass, ThisClass interpolableValue, Target defaultValue, Closure<Boolean> ignoreIf, Closure<Target> postProcess) {
      InterpolableClass interpolable = (InterpolableClass)interpolableValue ?: interpolableClass.newInstance()
      interpolable.init(
        new Supplier<Target>() {
          @Override
          Target get() {
            defaultValue
          }
        },
        ignoreIf,
        postProcess
      )
    }
  }
}
