package com.github.hashicorp.packer.engine.types

import com.fasterxml.jackson.annotation.JsonCreator
import com.github.hashicorp.packer.engine.exceptions.InvalidRawValueClassException
import com.github.hashicorp.packer.engine.exceptions.ObjectAlreadyInterpolatedWithFixedContextException
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
import groovy.transform.Synchronized
import sun.plugin.dom.exception.InvalidStateException

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

  @InheritConstructors
  private abstract static class AbstractInterpolableValue<
    Source,
    Target extends Serializable,
    ThisClass extends InterpolableValue<Source, Target, ThisClass>,
    InterpolableClass extends Interpolable<Source, Target, ThisClass, InterpolableClass, InitializedClass, AlreadyInterpolatedClass> & ThisClass,
    InitializedClass extends Initialized<Source, Target, ThisClass, InterpolableClass, InitializedClass, AlreadyInterpolatedClass> & ThisClass,
    AlreadyInterpolatedClass extends AlreadyInterpolated<Source, Target, ThisClass, InterpolableClass, InitializedClass, AlreadyInterpolatedClass> & ThisClass
  > extends AbstractInterpolableObject<AbstractInterpolableObject> implements InterpolableValue<Source, Target, ThisClass> {
    @Override
    final AbstractInterpolableObject interpolate(Context context) {
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
    static final Class<Source> SOURCE_CLASS = (Class<Source>)new TypeToken<Source>(this.class) { }.rawType
    @SuppressWarnings('UnstableApiUsage')
    static final Class<InterpolableClass> INTERPOLABLE_CLASS = (Class<InterpolableClass>)new TypeToken<InterpolableClass>(this.class) { }.rawType
    @SuppressWarnings('UnstableApiUsage')
    static final Class<InitializedClass> INITIALIZED_CLASS = (Class<InitializedClass>)new TypeToken<InitializedClass>(this.class) { }.rawType

    private volatile /* TODO */ Source rawValue

    // This is required for initWithDefault
    protected Interpolable() {
      super()
      this.@rawValue = null
    }

    protected Interpolable(Source rawValue, boolean readOnly) {
      super(readOnly)
      this.@rawValue = rawValue
    }

    // This is public so that it can be simply inherited by implementors // TOTHINK
    @JsonCreator
    Interpolable(Source rawValue) {
      this(rawValue, false)
    }

    @Override
    final Source getRawValue() {
      this.@rawValue
    }

    @Override
    final void setRawValue(Source rawValue) {
      if (readOnly) {
        throw new ReadOnlyPropertyException('rawValue', this.class.canonicalName) // TOTHINK
      }
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
      doInterpolatePrimitive context, this.@rawValue
    }

    protected static /* TOTEST */ Target doInterpolatePrimitive(Context context, Object rawValue) {
      throw new InvalidRawValueClassException(rawValue)
    }

    @Override
    final ThisClass interpolateValue(Context context, InterpolableObject delegate) {
      throw new InvalidStateException('Object is not initialized yet')
    }

    @Override
    final Target get() {
      throw new ValueNotInterpolatedYetException()
    }

    private final InitializedClass init(Supplier<Target> defaultValueSupplier, Closure<Boolean> ignoreIf, Closure<Target> postProcess) {
      INITIALIZED_CLASS.getConstructor(INTERPOLABLE_CLASS, Supplier<Target>, Closure<Boolean>, Closure<Target>, boolean).newInstance(this, defaultValueSupplier, ignoreIf, postProcess, readOnly)
    }

    @Override
    protected final InterpolableClass getAsReadOnly() {
      INTERPOLABLE_CLASS.getConstructor(SOURCE_CLASS, boolean).newInstance(this.@rawValue, true)
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
    static final Class<InterpolableClass> INTERPOLABLE_CLASS = (Class<InterpolableClass>)new TypeToken<InterpolableClass>(this.class) { }.rawType
    @SuppressWarnings('UnstableApiUsage')
    static final Class<InitializedClass> INITIALIZED_CLASS = (Class<InitializedClass>)new TypeToken<InitializedClass>(this.class) { }.rawType
    @SuppressWarnings('UnstableApiUsage')
    static final Class<AlreadyInterpolatedClass> ALREADY_INTERPOLATED_CLASS = (Class<AlreadyInterpolatedClass>)new TypeToken<AlreadyInterpolatedClass>(this.class) { }.rawType

    private final InterpolableClass interpolable

    private final Supplier<Target> defaultValueSupplier

    private final Closure<Boolean> ignoreIf

    private final Closure<Target> postProcess

    protected Initialized(InterpolableClass interpolable, Supplier<Target> defaultValueSupplier, Closure<Boolean> ignoreIf, Closure<Target> postProcess, boolean readOnly) {
      super(readOnly)
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
      this.@interpolable.rawValue
    }

    @Override
    @Synchronized
    final void setRawValue(Source rawValue) {
      if (readOnly) {
        throw new ReadOnlyPropertyException('rawValue', this.class.canonicalName)
      }
      this.@interpolable.rawValue = rawValue
    }

    @Synchronized
    private final InterpolableClass getRawValueAsReadOnly() {
      (InterpolableClass)this.@interpolable.asReadOnly()
    }

    @Override
    @Synchronized
    ThisClass interpolateValue(Context context, InterpolableObject delegate) {
      ALREADY_INTERPOLATED_CLASS.getConstructor(Object).newInstance(
        Suppliers.memoize(new Supplier<Target>() {
          private final InterpolableClass interpolable = rawValueAsReadOnly

          @Override
          @Synchronized
          Target get() {
            if (Initialized.this.@ignoreIf != null) {
              Closure<Boolean> ignoreIf = (Closure<Boolean>)Initialized.this.@ignoreIf.clone()
              ignoreIf.delegate = delegate
              if (ignoreIf.call() == Boolean.TRUE) {
                return null
              }
            }
            if (interpolable.rawValue != null) {
              Target interpolatedValue = interpolable.doInterpolatePrimitive(context)
              if (interpolatedValue != null) {
                if (Initialized.this.@postProcess) {
                  interpolatedValue = Initialized.this.@postProcess.call(interpolatedValue)
                }
                return interpolatedValue
              }
            }
            return Initialized.this.@defaultValueSupplier.get()
          }
        })
      )
    }

    @Override
    final Target get() {
      throw new ValueNotInterpolatedYetException()
    }

    @Override
    protected final InitializedClass getAsReadOnly() {
      INITIALIZED_CLASS.getConstructor(INTERPOLABLE_CLASS, Supplier<Target>, Closure<Boolean>, Closure<Target>, boolean).newInstance(this, defaultValueSupplier, ignoreIf, postProcess, readOnly)
    }
  }

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
      throw new ObjectAlreadyInterpolatedWithFixedContextException()
    }

    @Override
    final void setRawValue(Source rawValue) {
      throw new ObjectAlreadyInterpolatedWithFixedContextException()
    }

    private Object interpolatedValue
    // ThisClass constructor is required for Externalizable and AutoClone
    protected AlreadyInterpolated() {
      super(true)
    }

    AlreadyInterpolated(Object interpolatedValue) {
      this()
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
    final ThisClass interpolateValue(Context context, InterpolableObject delegate) {
      throw new ObjectAlreadyInterpolatedWithFixedContextException()
    }
    /**
     * @serial Interpolated value
     */
    @Override
    final Target get() {
      Supplier<Target>.isInstance(this.@interpolatedValue) ? ((Supplier<Target>)this.@interpolatedValue).get() : (Target)this.@interpolatedValue
    }

    @Override
    protected final AlreadyInterpolatedClass getAsReadOnly() {
      // This will never be called, so it makes no difference what we return here
      null // (AlreadyInterpolatedClass)this
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
      InterpolableClass interpolable = (InterpolableClass)interpolableValue ?: interpolableClass.getConstructor().newInstance()
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
      InterpolableClass interpolable = (InterpolableClass)interpolableValue ?: interpolableClass.getConstructor().newInstance()
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
