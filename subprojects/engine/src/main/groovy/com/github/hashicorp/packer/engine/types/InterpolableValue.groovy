package com.github.hashicorp.packer.engine.types

import com.fasterxml.jackson.annotation.JsonCreator
import com.github.hashicorp.packer.engine.exceptions.InvalidRawValueClass
import com.github.hashicorp.packer.engine.exceptions.ObjectAlreadyInterpolatedWithFixedContext
import com.github.hashicorp.packer.engine.exceptions.ValueNotInterpolatedYet
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
import groovy.transform.Synchronized
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

  @AutoClone(style = AutoCloneStyle.SIMPLE) // TODO
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

  @AutoClone(style = AutoCloneStyle.SIMPLE) // TODO
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

    // This is required for initWithDefault
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
      throw new ValueNotInterpolatedYet()
    }

    private final InitializedClass init(Supplier<Target> defaultValueSupplier, Closure<Boolean> ignoreIf, Closure<Target> postProcess) {
      INITIALIZED_CLASS.newInstance(this, defaultValueSupplier, ignoreIf, postProcess)
    }
  }

  @AutoClone(style = AutoCloneStyle.SIMPLE) // TODO
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
    @Synchronized
    void setRawValue(Source rawValue) {
      this.interpolable.rawValue = rawValue
    }

    @Override
    @Synchronized
    ThisClass interpolateValue(Context context, InterpolableObject delegate) {
      AlreadyInterpolatedClass result = ALREADY_INTERPOLATED_CLASS.newInstance()
      result.interpolatedValue = Suppliers.memoize(new Supplier<Target>() {
        final InterpolableClass interpolable = (InterpolableClass)Initialized.this.interpolable.clone() // TODO: read-only objects

        @Override
        @Synchronized
        Target get() {
          if (ignoreIf != null) {
            Closure<Boolean> ignoreIf = (Closure<Boolean>) ignoreIf.clone()
            ignoreIf.delegate = delegate
            if (ignoreIf.call() == Boolean.TRUE) {
              return null
            }
          }
          if (rawValue != null) {
            Target interpolatedValue = interpolable.doInterpolatePrimitive(context)
            if (interpolatedValue != null) {
              if (postProcess) {
                interpolatedValue = postProcess.call(interpolatedValue)
              }
              return interpolatedValue
            }
          }
          return defaultValueSupplier.get()
        }
      })
      result
    }

    @Override
    final Target get() {
      throw new ValueNotInterpolatedYet()
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
      Supplier<Target>.isInstance(interpolatedValue) ? ((Supplier<Target>)interpolatedValue).get() : (Target)interpolatedValue
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
