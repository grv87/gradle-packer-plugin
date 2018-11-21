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
import groovy.transform.Synchronized
import java.util.concurrent.Semaphore

// equals is required for Gradle up-to-date checking
// @AutoExternalize(excludes = ['rawValue']) // TODO: Groovy 2.5.0
@CompileStatic
// Serializable and Externalizable are required for Gradle up-to-date checking
interface InterpolableValue<
  Source,
  Target extends Serializable,
  ThisInterface extends InterpolableValue<Source, Target, ThisInterface>
> extends InterpolableObject<ThisInterface, ThisInterface>, Supplier<Target> {
  @JsonValue
  Source getRawValue()

  void setRawValue(Source rawValue)

  private static class CommonExceptions {
    private static RuntimeException interpolateWithDelegateObject() {
      new UnsupportedOperationException('Value objects should be interpolated with deledate object')
    }

    private static RuntimeException objectNotInitialized() {
      new IllegalStateException('Object is not initialized yet')
    }
  }

  ThisInterface interpolateValue(Context context, AbstractInterpolableReadOnlyObject delegate)

  private abstract static class AbstractRawValue<
    Source,
    Target extends Serializable,
    ThisInterface extends InterpolableValue<Source, Target, ThisInterface>,
    ReadOnlyRawValueClass extends ReadOnlyRawValue<Source, Target, ThisInterface, ReadOnlyRawValueClass, ReadWriteRawValueClass, ReadOnlyInitializedClass, ReadWriteInitializedClass, AlreadyInterpolatedClass> & ThisInterface,
    ReadWriteRawValueClass extends ReadWriteRawValue<Source, Target, ThisInterface, ReadOnlyRawValueClass, ReadWriteRawValueClass, ReadOnlyInitializedClass, ReadWriteInitializedClass, AlreadyInterpolatedClass> & ThisInterface,
    ReadOnlyInitializedClass extends ReadOnlyInitialized<Source, Target, ThisInterface, ReadOnlyRawValueClass, ReadWriteRawValueClass, ReadOnlyInitializedClass, ReadWriteInitializedClass, AlreadyInterpolatedClass> & ThisInterface,
    ReadWriteInitializedClass extends ReadWriteInitialized<Source, Target, ThisInterface, ReadOnlyRawValueClass, ReadWriteRawValueClass, ReadOnlyInitializedClass, ReadWriteInitializedClass, AlreadyInterpolatedClass> & ThisInterface,
    AlreadyInterpolatedClass extends AlreadyInterpolated<Source, Target, ThisInterface, ReadOnlyRawValueClass, ReadWriteRawValueClass, ReadOnlyInitializedClass, ReadWriteInitializedClass, AlreadyInterpolatedClass> & ThisInterface,
    SourceRawValueClass extends AbstractRawValue<Source, Target, ThisInterface, ReadOnlyRawValueClass, ReadWriteRawValueClass, ReadOnlyInitializedClass, ReadWriteInitializedClass, AlreadyInterpolatedClass, SourceRawValueClass, TargetInitializedClass> & ThisInterface,
    TargetInitializedClass extends AbstractInitialized<Source, Target, ThisInterface, ReadOnlyRawValueClass, ReadWriteRawValueClass, ReadOnlyInitializedClass, ReadWriteInitializedClass, AlreadyInterpolatedClass, SourceRawValueClass, TargetInitializedClass> & ThisInterface
    > extends AbstractInterpolableObject<ReadOnlyRawValueClass, ReadWriteRawValueClass> implements InterpolableValue<Source, Target, ThisInterface> {
    @SuppressWarnings('UnstableApiUsage')
    private static final Class<Source> SOURCE_CLASS = (Class<Source>)new TypeToken<Source>(this.class) { }.rawType
    @SuppressWarnings('UnstableApiUsage')
    private static final Class<ReadOnlyRawValueClass> READ_ONLY_INTERPOLABLE_CLASS = (Class<ReadOnlyRawValueClass>)new TypeToken<ReadOnlyRawValueClass>(this.class) { }.rawType
    @SuppressWarnings('UnstableApiUsage')
    private static final Class<ReadWriteRawValueClass> INTERPOLABLE_CLASS = (Class<ReadWriteRawValueClass>)new TypeToken<ReadWriteRawValueClass>(this.class) { }.rawType
    @SuppressWarnings('UnstableApiUsage')
    static final Class<Supplier<Target>> TARGET_SUPPLIER_CLASS = (Class<Supplier<Target>>)new TypeToken<Supplier<Target>>(this.class) { }.rawType
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
    final ReadOnlyRawValueClass interpolate(Context context) {
      throw CommonExceptions.interpolateWithDelegateObject()
    }

    @Override
    final ThisInterface interpolateValue(Context context, AbstractInterpolableReadOnlyObject delegate) {
      throw CommonExceptions.objectNotInitialized()
    }

    @Override
    final Target get() {
      throw new ValueNotInterpolatedYetException()
    }

    protected abstract TargetInitializedClass init(Supplier<Target> defaultValueSupplier, Closure<Boolean> ignoreIf, Closure<Target> postProcess)
  }

  abstract static class ReadOnlyRawValue<
    Source,
    Target extends Serializable,
    ThisInterface extends InterpolableValue<Source, Target, ThisInterface>,
    ReadOnlyRawValueClass extends ReadOnlyRawValue<Source, Target, ThisInterface, ReadOnlyRawValueClass, ReadWriteRawValueClass, ReadOnlyInitializedClass, ReadWriteInitializedClass, AlreadyInterpolatedClass> & ThisInterface,
    ReadWriteRawValueClass extends ReadWriteRawValue<Source, Target, ThisInterface, ReadOnlyRawValueClass, ReadWriteRawValueClass, ReadOnlyInitializedClass, ReadWriteInitializedClass, AlreadyInterpolatedClass> & ThisInterface,
    ReadOnlyInitializedClass extends ReadOnlyInitialized<Source, Target, ThisInterface, ReadOnlyRawValueClass, ReadWriteRawValueClass, ReadOnlyInitializedClass, ReadWriteInitializedClass, AlreadyInterpolatedClass> & ThisInterface,
    ReadWriteInitializedClass extends ReadWriteInitialized<Source, Target, ThisInterface, ReadOnlyRawValueClass, ReadWriteRawValueClass, ReadOnlyInitializedClass, ReadWriteInitializedClass, AlreadyInterpolatedClass> & ThisInterface,
    AlreadyInterpolatedClass extends AlreadyInterpolated<Source, Target, ThisInterface, ReadOnlyRawValueClass, ReadWriteRawValueClass, ReadOnlyInitializedClass, ReadWriteInitializedClass, AlreadyInterpolatedClass> & ThisInterface
    > extends AbstractRawValue<Source, Target, ThisInterface, ReadOnlyRawValueClass, ReadWriteRawValueClass, ReadOnlyInitializedClass, ReadWriteInitializedClass, AlreadyInterpolatedClass, ReadOnlyRawValueClass, ReadOnlyInitializedClass> {
    @SuppressWarnings('UnstableApiUsage')
    private static final Class<ReadOnlyInitializedClass> READ_ONLY_INITIALIZED_CLASS = (Class<ReadOnlyInitializedClass>)new TypeToken<ReadOnlyInitializedClass>(this.class) { }.rawType

    private /*final*/ /* TODO */ Source rawValue

    // This is required for initWithDefault
    protected ReadOnlyRawValue() {
      this.@rawValue = null
    }
    // This is public so that it can be simply inherited by implementors // TOTHINK
    @JsonCreator
    ReadOnlyRawValue(Source rawValue) {
      this.@rawValue = rawValue
    }

    @Override
    final Source getRawValue() {
      this.@rawValue
    }

    @Override
    final void setRawValue(Source rawValue) {
      throw new ReadOnlyPropertyException('rawValue', this.class.canonicalName) // TOTHINK
    }

    @Override
    protected final ReadOnlyInitializedClass init(Supplier<Target> defaultValueSupplier, Closure<Boolean> ignoreIf, Closure<Target> postProcess) {
      READ_ONLY_INITIALIZED_CLASS.getConstructor(READ_ONLY_INTERPOLABLE_CLASS, TARGET_SUPPLIER_CLASS, BOOLEAN_CLOSURE_CLASS, TARGET_CLOSURE_CLASS).newInstance(this, defaultValueSupplier, ignoreIf, postProcess)
    }

    @Override
    final ReadOnlyRawValueClass asReadOnly() {
      (ReadOnlyRawValueClass)this
    }

    @Override
    final ReadWriteRawValueClass asReadWrite() {
      INTERPOLABLE_CLASS.getConstructor(SOURCE_CLASS).newInstance(this.@rawValue)
    }
  }

  abstract static class ReadWriteRawValue<
    Source,
    Target extends Serializable,
    ThisInterface extends InterpolableValue<Source, Target, ThisInterface>,
    ReadOnlyRawValueClass extends ReadOnlyRawValue<Source, Target, ThisInterface, ReadOnlyRawValueClass, ReadWriteRawValueClass, ReadOnlyInitializedClass, ReadWriteInitializedClass, AlreadyInterpolatedClass> & ThisInterface,
    ReadWriteRawValueClass extends ReadWriteRawValue<Source, Target, ThisInterface, ReadOnlyRawValueClass, ReadWriteRawValueClass, ReadOnlyInitializedClass, ReadWriteInitializedClass, AlreadyInterpolatedClass> & ThisInterface,
    ReadOnlyInitializedClass extends ReadOnlyInitialized<Source, Target, ThisInterface, ReadOnlyRawValueClass, ReadWriteRawValueClass, ReadOnlyInitializedClass, ReadWriteInitializedClass, AlreadyInterpolatedClass> & ThisInterface,
    ReadWriteInitializedClass extends ReadWriteInitialized<Source, Target, ThisInterface, ReadOnlyRawValueClass, ReadWriteRawValueClass, ReadOnlyInitializedClass, ReadWriteInitializedClass, AlreadyInterpolatedClass> & ThisInterface,
    AlreadyInterpolatedClass extends AlreadyInterpolated<Source, Target, ThisInterface, ReadOnlyRawValueClass, ReadWriteRawValueClass, ReadOnlyInitializedClass, ReadWriteInitializedClass, AlreadyInterpolatedClass> & ThisInterface
    > extends AbstractRawValue<Source, Target, ThisInterface, ReadOnlyRawValueClass, ReadWriteRawValueClass, ReadOnlyInitializedClass, ReadWriteInitializedClass, AlreadyInterpolatedClass, ReadWriteRawValueClass, ReadWriteInitializedClass> {
    @SuppressWarnings('UnstableApiUsage')
    private static final Class<ReadWriteInitializedClass> INITIALIZED_CLASS = (Class<ReadWriteInitializedClass>)new TypeToken<ReadWriteInitializedClass>(this.class) { }.rawType

    private volatile /* TODO */ Source rawValue

    // This is required for initWithDefault
    protected ReadWriteRawValue() {
      this.@rawValue = null
    }
    // This is public so that it can be simply inherited by implementors // TOTHINK
    @JsonCreator
    ReadWriteRawValue(Source rawValue) {
      this.@rawValue = rawValue
    }

    @Override
    final Source getRawValue() {
      this.@rawValue
    }

    @Override
    final void setRawValue(Source rawValue) {
      this.@rawValue = rawValue
    }

    @Override
    protected final ReadWriteInitializedClass init(Supplier<Target> defaultValueSupplier, Closure<Boolean> ignoreIf, Closure<Target> postProcess) {
      INITIALIZED_CLASS.getConstructor(INTERPOLABLE_CLASS, TARGET_SUPPLIER_CLASS, BOOLEAN_CLOSURE_CLASS, TARGET_CLOSURE_CLASS).newInstance(this, defaultValueSupplier, ignoreIf, postProcess)
    }

    @Override
    final ReadOnlyRawValueClass asReadOnly() {
      READ_ONLY_INTERPOLABLE_CLASS.getConstructor(SOURCE_CLASS).newInstance(this.@rawValue)
    }

    @Override
    final ReadWriteRawValueClass asReadWrite() {
      (ReadWriteRawValueClass)this
    }
  }

  abstract private static class AbstractInitialized<
    Source,
    Target extends Serializable,
    ThisInterface extends InterpolableValue<Source, Target, ThisInterface>,
    ReadOnlyRawValueClass extends ReadOnlyRawValue<Source, Target, ThisInterface, ReadOnlyRawValueClass, ReadWriteRawValueClass, ReadOnlyInitializedClass, ReadWriteInitializedClass, AlreadyInterpolatedClass> & ThisInterface,
    ReadWriteRawValueClass extends ReadWriteRawValue<Source, Target, ThisInterface, ReadOnlyRawValueClass, ReadWriteRawValueClass, ReadOnlyInitializedClass, ReadWriteInitializedClass, AlreadyInterpolatedClass> & ThisInterface,
    ReadOnlyInitializedClass extends ReadOnlyInitialized<Source, Target, ThisInterface, ReadOnlyRawValueClass, ReadWriteRawValueClass, ReadOnlyInitializedClass, ReadWriteInitializedClass, AlreadyInterpolatedClass> & ThisInterface,
    ReadWriteInitializedClass extends ReadWriteInitialized<Source, Target, ThisInterface, ReadOnlyRawValueClass, ReadWriteRawValueClass, ReadOnlyInitializedClass, ReadWriteInitializedClass, AlreadyInterpolatedClass> & ThisInterface,
    AlreadyInterpolatedClass extends AlreadyInterpolated<Source, Target, ThisInterface, ReadOnlyRawValueClass, ReadWriteRawValueClass, ReadOnlyInitializedClass, ReadWriteInitializedClass, AlreadyInterpolatedClass> & ThisInterface,
    SourceRawValueClass extends AbstractRawValue<Source, Target, ThisInterface, ReadOnlyRawValueClass, ReadWriteRawValueClass, ReadOnlyInitializedClass, ReadWriteInitializedClass, AlreadyInterpolatedClass, SourceRawValueClass, TargetInitializedClass> & ThisInterface,
    TargetInitializedClass extends AbstractInitialized<Source, Target, ThisInterface, ReadOnlyRawValueClass, ReadWriteRawValueClass, ReadOnlyInitializedClass, ReadWriteInitializedClass, AlreadyInterpolatedClass, SourceRawValueClass, TargetInitializedClass> & ThisInterface
    > extends AbstractInterpolableObject<ReadOnlyInitializedClass, ReadWriteInitializedClass> implements InterpolableValue<Source, Target, ThisInterface> {
    @SuppressWarnings('UnstableApiUsage')
    private static final Class<AlreadyInterpolatedClass> ALREADY_INTERPOLATED_CLASS = (Class<AlreadyInterpolatedClass>)new TypeToken<AlreadyInterpolatedClass>(this.class) { }.rawType
    @SuppressWarnings('UnstableApiUsage')
    static final Class<Supplier<Target>> TARGET_SUPPLIER_CLASS = (Class<Supplier<Target>>)new TypeToken<Supplier<Target>>(this.class) { }.rawType
    @SuppressWarnings('UnstableApiUsage')
    private static final Class<Closure<Target>> TARGET_CLOSURE_CLASS = (Class<Closure<Target>>)new TypeToken<Closure<Target>>(this.class) { }.rawType
    @SuppressWarnings('UnstableApiUsage')
    private static final Class<Closure<Boolean>> BOOLEAN_CLOSURE_CLASS = (Class<Closure<Boolean>>)new TypeToken<Closure<Boolean>>(this.class) { }.rawType

    private final SourceRawValueClass interpolable

    private final Supplier<Target> defaultValueSupplier

    private final Closure<Boolean> ignoreIf

    private final Closure<Target> postProcess

    protected AbstractInitialized(SourceRawValueClass interpolable, Supplier<Target> defaultValueSupplier, Closure<Boolean> ignoreIf, Closure<Target> postProcess) {
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

    protected abstract ReadOnlyRawValueClass getInterpolableAsReadOnly()

    @Override
    final ReadOnlyInitializedClass interpolate(Context context) {
      throw CommonExceptions.interpolateWithDelegateObject()
    }

    @Override
    @Synchronized
    ThisInterface interpolateValue(Context context, AbstractInterpolableReadOnlyObject delegate) {
      ALREADY_INTERPOLATED_CLASS.getConstructor(Object).newInstance(
        Suppliers.memoize(new Supplier<Target>() {
          private final ReadOnlyRawValueClass interpolable = interpolableAsReadOnly
          private final Semaphore semaphore = new Semaphore(1)

          @Override
          @Synchronized
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
                Target interpolatedValue = interpolable.doInterpolatePrimitive(context)
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

  @InheritConstructors
  abstract static class ReadOnlyInitialized<
    Source,
    Target extends Serializable,
    ThisInterface extends InterpolableValue<Source, Target, ThisInterface>,
    ReadOnlyRawValueClass extends ReadOnlyRawValue<Source, Target, ThisInterface, ReadOnlyRawValueClass, ReadWriteRawValueClass, ReadOnlyInitializedClass, ReadWriteInitializedClass, AlreadyInterpolatedClass> & ThisInterface,
    ReadWriteRawValueClass extends ReadWriteRawValue<Source, Target, ThisInterface, ReadOnlyRawValueClass, ReadWriteRawValueClass, ReadOnlyInitializedClass, ReadWriteInitializedClass, AlreadyInterpolatedClass> & ThisInterface,
    ReadOnlyInitializedClass extends ReadOnlyInitialized<Source, Target, ThisInterface, ReadOnlyRawValueClass, ReadWriteRawValueClass, ReadOnlyInitializedClass, ReadWriteInitializedClass, AlreadyInterpolatedClass> & ThisInterface,
    ReadWriteInitializedClass extends ReadWriteInitialized<Source, Target, ThisInterface, ReadOnlyRawValueClass, ReadWriteRawValueClass, ReadOnlyInitializedClass, ReadWriteInitializedClass, AlreadyInterpolatedClass> & ThisInterface,
    AlreadyInterpolatedClass extends AlreadyInterpolated<Source, Target, ThisInterface, ReadOnlyRawValueClass, ReadWriteRawValueClass, ReadOnlyInitializedClass, ReadWriteInitializedClass, AlreadyInterpolatedClass> & ThisInterface
    > extends AbstractInitialized<Source, Target, ThisInterface, ReadOnlyRawValueClass, ReadWriteRawValueClass, ReadOnlyInitializedClass, ReadWriteInitializedClass, AlreadyInterpolatedClass, ReadOnlyRawValueClass, ReadOnlyInitializedClass> {
    @SuppressWarnings('UnstableApiUsage')
    private static final Class<ReadWriteRawValueClass> INTERPOLABLE_CLASS = (Class<ReadWriteRawValueClass>)new TypeToken<ReadWriteRawValueClass>(this.class) { }.rawType
    @SuppressWarnings('UnstableApiUsage')
    private static final Class<ReadWriteInitializedClass> INITIALIZED_CLASS = (Class<ReadWriteInitializedClass>)new TypeToken<ReadWriteInitializedClass>(this.class) { }.rawType
    @Override
    final void setRawValue(Source rawValue) {
      throw new ReadOnlyPropertyException('rawValue', this.class.canonicalName) // TOTHINK
    }

    protected final ReadOnlyRawValueClass getInterpolableAsReadOnly() {
      this.@interpolable
    }

    @Override
    final ReadOnlyInitializedClass asReadOnly() {
      (ReadOnlyInitializedClass)this
    }

    @Override
    final ReadWriteInitializedClass asReadWrite() {
      INITIALIZED_CLASS.getConstructor(INTERPOLABLE_CLASS, TARGET_SUPPLIER_CLASS, BOOLEAN_CLOSURE_CLASS, TARGET_CLOSURE_CLASS).newInstance(this.@interpolable.asReadWrite(), defaultValueSupplier, ignoreIf, postProcess)
    }
  }

  @InheritConstructors
  abstract static class ReadWriteInitialized<
    Source,
    Target extends Serializable,
    ThisInterface extends InterpolableValue<Source, Target, ThisInterface>,
    ReadOnlyRawValueClass extends ReadOnlyRawValue<Source, Target, ThisInterface, ReadOnlyRawValueClass, ReadWriteRawValueClass, ReadOnlyInitializedClass, ReadWriteInitializedClass, AlreadyInterpolatedClass> & ThisInterface,
    ReadWriteRawValueClass extends ReadWriteRawValue<Source, Target, ThisInterface, ReadOnlyRawValueClass, ReadWriteRawValueClass, ReadOnlyInitializedClass, ReadWriteInitializedClass, AlreadyInterpolatedClass> & ThisInterface,
    ReadOnlyInitializedClass extends ReadOnlyInitialized<Source, Target, ThisInterface, ReadOnlyRawValueClass, ReadWriteRawValueClass, ReadOnlyInitializedClass, ReadWriteInitializedClass, AlreadyInterpolatedClass> & ThisInterface,
    ReadWriteInitializedClass extends ReadWriteInitialized<Source, Target, ThisInterface, ReadOnlyRawValueClass, ReadWriteRawValueClass, ReadOnlyInitializedClass, ReadWriteInitializedClass, AlreadyInterpolatedClass> & ThisInterface,
    AlreadyInterpolatedClass extends AlreadyInterpolated<Source, Target, ThisInterface, ReadOnlyRawValueClass, ReadWriteRawValueClass, ReadOnlyInitializedClass, ReadWriteInitializedClass, AlreadyInterpolatedClass> & ThisInterface
    > extends AbstractInitialized<Source, Target, ThisInterface, ReadOnlyRawValueClass, ReadWriteRawValueClass, ReadOnlyInitializedClass, ReadWriteInitializedClass, AlreadyInterpolatedClass, ReadWriteRawValueClass, ReadWriteInitializedClass> {
    @SuppressWarnings('UnstableApiUsage')
    private static final Class<ReadOnlyRawValueClass> READ_ONLY_INTERPOLABLE_CLASS = (Class<ReadOnlyRawValueClass>)new TypeToken<ReadOnlyRawValueClass>(this.class) { }.rawType
    @SuppressWarnings('UnstableApiUsage')
    private static final Class<ReadOnlyInitializedClass> READ_ONLY_INITIALIZED_CLASS = (Class<ReadOnlyInitializedClass>)new TypeToken<ReadOnlyInitializedClass>(this.class) { }.rawType

    @Override
    @Synchronized
    final void setRawValue(Source rawValue) {
      this.@interpolable.rawValue = rawValue
    }

    @Synchronized
    protected final ReadOnlyRawValueClass getInterpolableAsReadOnly() {
      (ReadOnlyRawValueClass)this.@interpolable.asReadOnly()
    }

    @Override
    final ReadOnlyInitializedClass asReadOnly() {
      READ_ONLY_INITIALIZED_CLASS.getConstructor(READ_ONLY_INTERPOLABLE_CLASS, TARGET_SUPPLIER_CLASS, BOOLEAN_CLOSURE_CLASS, TARGET_CLOSURE_CLASS).newInstance(interpolableAsReadOnly, defaultValueSupplier, ignoreIf, postProcess)
    }

    @Override
    final ReadWriteInitializedClass asReadWrite() {
      (ReadWriteInitializedClass)this
    }
  }

  @EqualsAndHashCode(includes = ['get'])
  abstract static class AlreadyInterpolated<
    Source,
    Target extends Serializable,
    ThisInterface extends InterpolableValue<Source, Target, ThisInterface>,
    ReadOnlyRawValueClass extends ReadOnlyRawValue<Source, Target, ThisInterface, ReadOnlyRawValueClass, ReadWriteRawValueClass, ReadOnlyInitializedClass, ReadWriteInitializedClass, AlreadyInterpolatedClass> & ThisInterface,
    ReadWriteRawValueClass extends ReadWriteRawValue<Source, Target, ThisInterface, ReadOnlyRawValueClass, ReadWriteRawValueClass, ReadOnlyInitializedClass, ReadWriteInitializedClass, AlreadyInterpolatedClass> & ThisInterface,
    ReadOnlyInitializedClass extends ReadOnlyInitialized<Source, Target, ThisInterface, ReadOnlyRawValueClass, ReadWriteRawValueClass, ReadOnlyInitializedClass, ReadWriteInitializedClass, AlreadyInterpolatedClass> & ThisInterface,
    ReadWriteInitializedClass extends ReadWriteInitialized<Source, Target, ThisInterface, ReadOnlyRawValueClass, ReadWriteRawValueClass, ReadOnlyInitializedClass, ReadWriteInitializedClass, AlreadyInterpolatedClass> & ThisInterface,
    AlreadyInterpolatedClass extends AlreadyInterpolated<Source, Target, ThisInterface, ReadOnlyRawValueClass, ReadWriteRawValueClass, ReadOnlyInitializedClass, ReadWriteInitializedClass, AlreadyInterpolatedClass> & ThisInterface
  > extends AbstractInterpolableReadOnlyObject<AlreadyInterpolatedClass, AlreadyInterpolatedClass> implements InterpolableValue<Source, Target, ThisInterface>, Externalizable {
    @SuppressWarnings('UnstableApiUsage')
    static final Class<Supplier<Target>> TARGET_SUPPLIER_CLASS = (Class<Supplier<Target>>)new TypeToken<Supplier<Target>>(this.class) { }.rawType

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
    final AlreadyInterpolatedClass interpolate(Context context) {
      throw CommonExceptions.interpolateWithDelegateObject()
    }

    @Override
    final ThisInterface interpolateValue(Context context, AbstractInterpolableReadOnlyObject delegate) {
      throw new ObjectAlreadyInterpolatedWithFixedContextException()
    }
    /**
     * @serial Interpolated value
     */
    @Override
    final Target get() {
      TARGET_SUPPLIER_CLASS.isInstance(this.@interpolatedValue) ? ((Supplier<Target>)this.@interpolatedValue).get() : (Target)this.@interpolatedValue
    }

    @Override
    final AlreadyInterpolatedClass asReadWrite() {
      throw new ObjectAlreadyInterpolatedWithFixedContextException()
    }
  }

  @PackageScope
  static class Utils {
    static boolean requiresInitialization(InterpolableValue interpolableValue) {
      interpolableValue == null || AbstractRawValue.isInstance(interpolableValue)
    }

    // ThisInterface is used to create instances with default values
    protected static final <
      Source,
      Target extends Serializable,
      ThisInterface extends InterpolableValue<Source, Target, ThisInterface>,
      AbstractRawValueClass extends AbstractRawValue<Source, Target, ThisInterface, ReadOnlyRawValueClass, ReadWriteRawValueClass, ReadOnlyInitializedClass, ReadWriteInitializedClass, AlreadyInterpolatedClass, ? extends AbstractRawValueClass, ? extends AbstractInitializedClass> & ThisInterface,
      ReadOnlyRawValueClass extends ReadOnlyRawValue<Source, Target, ThisInterface, ReadOnlyRawValueClass, ReadWriteRawValueClass, ReadOnlyInitializedClass, ReadWriteInitializedClass, AlreadyInterpolatedClass> & ThisInterface & AbstractRawValueClass,
      ReadWriteRawValueClass extends ReadWriteRawValue<Source, Target, ThisInterface, ReadOnlyRawValueClass, ReadWriteRawValueClass, ReadOnlyInitializedClass, ReadWriteInitializedClass, AlreadyInterpolatedClass> & ThisInterface & AbstractRawValueClass,
      AbstractInitializedClass extends AbstractInitialized<Source, Target, ThisInterface, ReadOnlyRawValueClass, ReadWriteRawValueClass, ReadOnlyInitializedClass, ReadWriteInitializedClass, AlreadyInterpolatedClass, ? extends AbstractRawValueClass, ? extends AbstractInitializedClass> & ThisInterface,
      ReadOnlyInitializedClass extends ReadOnlyInitialized<Source, Target, ThisInterface, ReadOnlyRawValueClass, ReadWriteRawValueClass, ReadOnlyInitializedClass, ReadWriteInitializedClass, AlreadyInterpolatedClass> & ThisInterface & AbstractInitializedClass,
      ReadWriteInitializedClass extends ReadWriteInitialized<Source, Target, ThisInterface, ReadOnlyRawValueClass, ReadWriteRawValueClass, ReadOnlyInitializedClass, ReadWriteInitializedClass, AlreadyInterpolatedClass> & ThisInterface & AbstractInitializedClass,
      AlreadyInterpolatedClass extends AlreadyInterpolated<Source, Target, ThisInterface, ReadOnlyRawValueClass, ReadWriteRawValueClass, ReadOnlyInitializedClass, ReadWriteInitializedClass, AlreadyInterpolatedClass> & ThisInterface
    > ThisInterface initWithDefault(boolean readOnly, Class<ReadOnlyRawValueClass> readOnlyRawValueClass, Class<ReadWriteRawValueClass> readWriteRawValueClass, ThisInterface interpolableValue, Supplier<Target> defaultValueSupplier, Closure<Boolean> ignoreIf, Closure<Target> postProcess) {
      AbstractRawValueClass interpolable = (AbstractRawValueClass)interpolableValue ?: (readOnly ? readOnlyRawValueClass : readWriteRawValueClass).getConstructor().newInstance()
      interpolable.init(defaultValueSupplier, ignoreIf, postProcess)
    }

    // ThisInterface is used to create instances with default values
    protected static final <
      Source,
      Target extends Serializable,
      ThisInterface extends InterpolableValue<Source, Target, ThisInterface>,
      AbstractRawValueClass extends AbstractRawValue<Source, Target, ThisInterface, ReadOnlyRawValueClass, ReadWriteRawValueClass, ReadOnlyInitializedClass, ReadWriteInitializedClass, AlreadyInterpolatedClass, ? extends AbstractRawValueClass, ? extends AbstractInitializedClass> & ThisInterface,
      ReadOnlyRawValueClass extends ReadOnlyRawValue<Source, Target, ThisInterface, ReadOnlyRawValueClass, ReadWriteRawValueClass, ReadOnlyInitializedClass, ReadWriteInitializedClass, AlreadyInterpolatedClass> & ThisInterface & AbstractRawValueClass,
      ReadWriteRawValueClass extends ReadWriteRawValue<Source, Target, ThisInterface, ReadOnlyRawValueClass, ReadWriteRawValueClass, ReadOnlyInitializedClass, ReadWriteInitializedClass, AlreadyInterpolatedClass> & ThisInterface & AbstractRawValueClass,
      AbstractInitializedClass extends AbstractInitialized<Source, Target, ThisInterface, ReadOnlyRawValueClass, ReadWriteRawValueClass, ReadOnlyInitializedClass, ReadWriteInitializedClass, AlreadyInterpolatedClass, ? extends AbstractRawValueClass, ? extends AbstractInitializedClass> & ThisInterface,
      ReadOnlyInitializedClass extends ReadOnlyInitialized<Source, Target, ThisInterface, ReadOnlyRawValueClass, ReadWriteRawValueClass, ReadOnlyInitializedClass, ReadWriteInitializedClass, AlreadyInterpolatedClass> & ThisInterface & AbstractInitializedClass,
      ReadWriteInitializedClass extends ReadWriteInitialized<Source, Target, ThisInterface, ReadOnlyRawValueClass, ReadWriteRawValueClass, ReadOnlyInitializedClass, ReadWriteInitializedClass, AlreadyInterpolatedClass> & ThisInterface & AbstractInitializedClass,
      AlreadyInterpolatedClass extends AlreadyInterpolated<Source, Target, ThisInterface, ReadOnlyRawValueClass, ReadWriteRawValueClass, ReadOnlyInitializedClass, ReadWriteInitializedClass, AlreadyInterpolatedClass> & ThisInterface
    > ThisInterface initWithDefault(boolean readOnly, Class<ReadOnlyRawValueClass> readOnlyRawValueClass, Class<ReadWriteRawValueClass> readWriteRawValueClass, ThisInterface interpolableValue, Target defaultValue, Closure<Boolean> ignoreIf, Closure<Target> postProcess) {
      AbstractRawValueClass interpolable = (AbstractRawValueClass)interpolableValue ?: (readOnly ? readOnlyRawValueClass : readWriteRawValueClass).getConstructor().newInstance()
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
