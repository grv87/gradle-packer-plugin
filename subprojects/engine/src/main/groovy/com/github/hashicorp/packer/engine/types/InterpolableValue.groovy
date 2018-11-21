package com.github.hashicorp.packer.engine.types

import com.fasterxml.jackson.annotation.JsonCreator
import com.github.hashicorp.packer.engine.exceptions.InvalidRawValueClassException
import com.github.hashicorp.packer.engine.exceptions.ObjectAlreadyInterpolatedWithFixedContextException
import com.github.hashicorp.packer.engine.exceptions.RecursiveInterpolationException
import com.github.hashicorp.packer.engine.exceptions.ValueNotInterpolatedYetException
import com.github.hashicorp.packer.template.Context
import com.google.common.base.Supplier
import com.google.common.base.Suppliers
import com.google.common.reflect.TypeToken
import groovy.transform.CompileStatic
import groovy.transform.CompileDynamic
import groovy.transform.EqualsAndHashCode
import com.fasterxml.jackson.annotation.JsonValue
import groovy.transform.InheritConstructors
import groovy.transform.PackageScope

import java.lang.reflect.Constructor
import java.util.concurrent.Semaphore

// equals is required for Gradle up-to-date checking
// @AutoExternalize(excludes = ['rawValue']) // TODO: Groovy 2.5.0
@CompileStatic
// Serializable and Externalizable are required for Gradle up-to-date checking
interface InterpolableValue<
  Source,
  Target extends Serializable,
  ThisInterface extends InterpolableValue<Source, Target, ThisInterface>
> extends InterpolableObject<ThisInterface>, Supplier<Target> {
  @JsonValue
  Source getRawValue()

  private static class CommonExceptions {
    private static RuntimeException interpolateWithDelegateObject() {
      new UnsupportedOperationException('Value objects should be interpolated with deledate object')
    }

    private static RuntimeException objectNotInitialized() {
      new IllegalStateException('Object is not initialized yet')
    }
  }

  // delegate should be readOnly, or something terrible could happen
  ThisInterface interpolateValue(Context context, InterpolableObject delegate)

  protected abstract static class RawValue<
    Source,
    Target extends Serializable,
    ThisInterface extends InterpolableValue<Source, Target, ThisInterface>,
    AlreadyInterpolatedClass extends AlreadyInterpolated<Source, Target, ThisInterface> & ThisInterface,
    InitializedClass extends Initialized<Source, Target, ThisInterface, AlreadyInterpolatedClass, InitializedClass> & ThisInterface
    > implements InterpolableValue<Source, Target, ThisInterface> {
    @SuppressWarnings('UnstableApiUsage')
    private static final Class<InitializedClass> INITIALIZED_CLASS = (Class<InitializedClass>)new TypeToken<InitializedClass>(this.class) { }.rawType
    @SuppressWarnings('UnstableApiUsage')
    private static final Class<Supplier<Target>> TARGET_SUPPLIER_CLASS = (Class<Supplier<Target>>)new TypeToken<Supplier<Target>>(this.class) { }.rawType
    @SuppressWarnings('UnstableApiUsage')
    private static final Class<Closure<Target>> TARGET_CLOSURE_CLASS = (Class<Closure<Target>>)new TypeToken<Closure<Target>>(this.class) { }.rawType
    @SuppressWarnings('UnstableApiUsage')
    private static final Class<Closure<Boolean>> BOOLEAN_CLOSURE_CLASS = (Class<Closure<Boolean>>)new TypeToken<Closure<Boolean>>(this.class) { }.rawType

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
      throw new InvalidRawValueClassException(rawValue)
    }

    @Override
    final ThisInterface interpolate(Context context) {
      throw CommonExceptions.interpolateWithDelegateObject()
    }

    @Override
    final ThisInterface interpolateValue(Context context, InterpolableObject delegate) {
      throw CommonExceptions.objectNotInitialized()
    }

    @Override
    final Target get() {
      throw new ValueNotInterpolatedYetException()
    }

    private /*final*/ /* TODO */ Source rawValue

    // This is public so that it can be simply inherited by implementors // TOTHINK
    @JsonCreator
    RawValue(Source rawValue) {
      this.@rawValue = rawValue
    }

    @Override
    final Source getRawValue() {
      this.@rawValue
    }

    private static final Constructor<InitializedClass> INITIALIZED_CLASS_CONSTRUCTOR = INITIALIZED_CLASS.getConstructor(/*RAW_VALUE_CLASS*/ RawValue, TARGET_SUPPLIER_CLASS, BOOLEAN_CLOSURE_CLASS, TARGET_CLOSURE_CLASS)

    private InitializedClass init(Supplier<Target> defaultValueSupplier, Closure<Boolean> ignoreIf, Closure<Target> postProcess) {
      INITIALIZED_CLASS_CONSTRUCTOR.newInstance(this, defaultValueSupplier, ignoreIf, postProcess)
    }

    private InitializedClass init(Target defaultValue, Closure<Boolean> ignoreIf, Closure<Target> postProcess) {
      INITIALIZED_CLASS_CONSTRUCTOR.newInstance(this, new Supplier<Target>() {
        @Override
        Target get() {
          defaultValue
        }
      }, ignoreIf, postProcess)
    }
  }

  protected abstract static class Initialized<
    Source,
    Target extends Serializable,
    ThisInterface extends InterpolableValue<Source, Target, ThisInterface>,
    AlreadyInterpolatedClass extends AlreadyInterpolated<Source, Target, ThisInterface> & ThisInterface,
    InitializedClass extends Initialized<Source, Target, ThisInterface, AlreadyInterpolatedClass, InitializedClass> & ThisInterface
  > implements InterpolableValue<Source, Target, ThisInterface> {
    @SuppressWarnings('UnstableApiUsage')
    private static final Class<AlreadyInterpolatedClass> ALREADY_INTERPOLATED_CLASS = (Class<AlreadyInterpolatedClass>)new TypeToken<AlreadyInterpolatedClass>(this.class) { }.rawType

    /*
     * WORKAROUND:
     * We can't have RawValueClass here and later in this class since Groovy doesn't support cyclic generics
     * (at least 2.4.12)
     * TODO: test on 2.5/3.0 and report an issue
     * <grv87 2018-11-22>
     */
    private final /*RawValueClass*/RawValue interpolable

    private final Supplier<Target> defaultValueSupplier

    private final Closure<Boolean> ignoreIf

    private final Closure<Target> postProcess

    protected Initialized(/*RawValueClass*/RawValue interpolable, Supplier<Target> defaultValueSupplier, Closure<Boolean> ignoreIf, Closure<Target> postProcess) {
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
      (Source)this.@interpolable.rawValue
    }

    @Override
    final ThisInterface interpolate(Context context) {
      throw CommonExceptions.interpolateWithDelegateObject()
    }

    @Override
    ThisInterface interpolateValue(Context context, InterpolableObject delegate) {
      ALREADY_INTERPOLATED_CLASS.getConstructor(Object).newInstance(
        Suppliers.memoize(new Supplier<Target>() {
          private final Semaphore semaphore = new Semaphore(1)

          @Override
          Target get() {
            if (!semaphore.tryAcquire()) {
              throw new RecursiveInterpolationException()
            }
            try {
              if (AbstractInitialized.this.@ignoreIf != null) {
                Closure<Boolean> ignoreIf = (Closure<Boolean>) AbstractInitialized.this.@ignoreIf.clone()
                ignoreIf.delegate = delegate
                if (ignoreIf.call() == Boolean.TRUE) {
                  return null
                }
              }
              if (interpolable.rawValue != null) {
                Target interpolatedValue = (Target)interpolable.doInterpolatePrimitive(context)
                if (interpolatedValue != null) {
                  if (AbstractInitialized.this.@postProcess) {
                    interpolatedValue = AbstractInitialized.this.@postProcess.call(interpolatedValue)
                  }
                  return interpolatedValue
                }
              }
              return AbstractInitialized.this.@defaultValueSupplier.get()
            } finally {
              semaphore.release()
            }
          }
        })
      )
    }

    @Override
    final Target get() {
      throw new ValueNotInterpolatedYetException()
    }
  }

  @EqualsAndHashCode(includes = ['get'])
  protected abstract static class AlreadyInterpolated<
    Source,
    Target extends Serializable,
    ThisInterface extends InterpolableValue<Source, Target, ThisInterface>
  > implements InterpolableValue<Source, Target, ThisInterface>, Externalizable {
    @SuppressWarnings('UnstableApiUsage')
    static final Class<Supplier<Target>> TARGET_SUPPLIER_CLASS = (Class<Supplier<Target>>)new TypeToken<Supplier<Target>>(this.class) { }.rawType

    @JsonValue
    @Override
    final Source getRawValue() {
      throw new ObjectAlreadyInterpolatedWithFixedContextException()
    }

    private Object interpolatedValue
    // ThisInterface constructor is required for Externalizable and AutoClone
    protected AlreadyInterpolated() { }

    AlreadyInterpolated(Object interpolatedValue) {
      this.@interpolatedValue = interpolatedValue
    }

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
      this.@interpolatedValue = (Target)oin.readObject()
    }

    @Override
    final ThisInterface interpolate(Context context) {
      throw CommonExceptions.interpolateWithDelegateObject()
    }

    @Override
    final ThisInterface interpolateValue(Context context, InterpolableObject delegate) {
      throw new ObjectAlreadyInterpolatedWithFixedContextException()
    }
    /**
     * @serial Interpolated value
     */
    @Override
    final Target get() {
      TARGET_SUPPLIER_CLASS.isInstance(this.@interpolatedValue) ? ((Supplier<Target>)this.@interpolatedValue).get() : (Target)this.@interpolatedValue
    }
  }

  @PackageScope
  static class Utils {
    static boolean requiresInitialization(InterpolableValue interpolableValue) {
      interpolableValue == null || RawValue.isInstance(interpolableValue)
    }

    // ThisInterface is used to create instances with default values
    protected static final <
      Source,
      Target extends Serializable,
      ThisInterface extends InterpolableValue<Source, Target, ThisInterface>,
      AlreadyInterpolatedClass extends AlreadyInterpolated<Source, Target, ThisInterface> & ThisInterface,
      InitializedClass extends Initialized<Source, Target, ThisInterface, AlreadyInterpolatedClass, InitializedClass> & ThisInterface,
      RawValueClass extends RawValue<Source, Target, ThisInterface, AlreadyInterpolatedClass, InitializedClass, RawValueClass> & ThisInterface
    > ThisInterface initWithDefault(Class<RawValueClass> rawValueClass, ThisInterface interpolableValue, Supplier<Target> defaultValueSupplier, Closure<Boolean> ignoreIf, Closure<Target> postProcess) {
      RawValueClass interpolable = (RawValueClass)interpolableValue ?: rawValueClass.getConstructor().newInstance()
      interpolable.init defaultValueSupplier, ignoreIf, postProcess
    }

    // ThisInterface is used to create instances with default values
    protected static final <
      Source,
      Target extends Serializable,
      ThisInterface extends InterpolableValue<Source, Target, ThisInterface>,
      AlreadyInterpolatedClass extends AlreadyInterpolated<Source, Target, ThisInterface> & ThisInterface,
      InitializedClass extends Initialized<Source, Target, ThisInterface, AlreadyInterpolatedClass, InitializedClass> & ThisInterface,
      RawValueClass extends RawValue<Source, Target, ThisInterface, AlreadyInterpolatedClass, InitializedClass, RawValueClass> & ThisInterface
    > ThisInterface initWithDefault(Class<RawValueClass> rawValueClass, ThisInterface interpolableValue, Target defaultValue, Closure<Boolean> ignoreIf, Closure<Target> postProcess) {
      RawValueClass interpolable = (RawValueClass)interpolableValue ?: rawValueClass.getConstructor().newInstance()
      interpolable.init defaultValue, ignoreIf, postProcess
    }
  }
}
