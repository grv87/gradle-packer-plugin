package com.github.hashicorp.packer.engine.types

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.databind.Module
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.module.SimpleModule
import com.github.hashicorp.packer.engine.exceptions.InvalidRawValueClassException
import com.github.hashicorp.packer.engine.exceptions.ObjectAlreadyInterpolatedWithFixedContextException
import com.github.hashicorp.packer.engine.exceptions.RecursiveInterpolationException
import com.github.hashicorp.packer.engine.exceptions.ValueNotInterpolatedYetException
import com.github.hashicorp.packer.engine.utils.ModuleProvider
import com.github.hashicorp.packer.engine.utils.Mutability
import com.github.hashicorp.packer.engine.utils.ObjectMapperFacade
import com.github.hashicorp.packer.template.Context
import com.google.common.base.Supplier
import com.google.common.reflect.TypeToken
import groovy.transform.CompileStatic
import groovy.transform.CompileDynamic
// import groovy.transform.EqualsAndHashCode
import groovy.transform.Synchronized
import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.databind.JsonSerializer
import com.fasterxml.jackson.databind.SerializerProvider
import java.lang.reflect.Constructor
import java.util.concurrent.Semaphore

// @AutoExternalize(excludes = ['raw']) // TODO: Groovy 2.5.0
@CompileStatic
// Serializable is required for Gradle up-to-date checking
interface InterpolableValue<
  Source,
  Target extends Serializable,
  ThisInterface extends InterpolableValue<Source, Target, ThisInterface>
> extends InterpolableObject<ThisInterface> {
  Source getRaw()

  void setRaw(Source raw)

  ThisInterface interpolateValue(Context context, Supplier<Target> defaultValueSupplier, Closure<Boolean> ignoreIf, Closure<Target> postProcess)

  ThisInterface interpolateValue(Context context, Target defaultValue, Closure<Boolean> ignoreIf, Closure<Target> postProcess)

  ThisInterface interpolateValue(Context context, Supplier<Target> defaultValueSupplier, Closure<Boolean> ignoreIf)

  ThisInterface interpolateValue(Context context, Target defaultValue, Closure<Boolean> ignoreIf)

  ThisInterface interpolateValue(Context context, Supplier<Target> defaultValueSupplier)

  ThisInterface interpolateValue(Context context, Target defaultValue)

  ThisInterface interpolateValue(Context context)

  // Note: Map variants are provided but not used, since it is unnecessary map creation and dissection
  ThisInterface interpolateValue(Map<String, ?> args, Closure<Target> postProcess)

  ThisInterface interpolateValue(Map<String, ?> args)

  Target getInterpolated()

  private abstract static class Abstract<
    Source,
    Target extends Serializable,
    ThisInterface extends InterpolableValue<Source, Target, ThisInterface>
  > implements InterpolableValue<Source, Target, ThisInterface> {
    // TODO: With final error: You are not allowed to override the final method interpolate(com.github.hashicorp.packer.template.Context -> com.github.hashicorp.packer.template.Context) from class 'com.github.hashicorp.packer.engine.types.InterpolableValue$Abstract'.
    @Override
    /*final*/ ThisInterface interpolate(Context context) {
      throw CommonExceptions.interpolateWithDefault()
    }

    @Override
    final ThisInterface interpolateValue(Context context, Supplier<Target> defaultValueSupplier, Closure<Boolean> ignoreIf) {
      interpolateValue(context, defaultValueSupplier, ignoreIf, null)
    }

    @Override
    final ThisInterface interpolateValue(Context context, Target defaultValue, Closure<Boolean> ignoreIf) {
      interpolateValue(context, defaultValue, ignoreIf, null)
    }

    @Override
    final ThisInterface interpolateValue(Context context, Supplier<Target> defaultValueSupplier) {
      interpolateValue(context, defaultValueSupplier, null, null)
    }

    @Override
    final ThisInterface interpolateValue(Context context, Target defaultValue) {
      interpolateValue(context, defaultValue, null, null)
    }

    @Override
    final ThisInterface interpolateValue(Context context) {
      interpolateValue(context, (Target)null, null, null)
    }

    @Override
    final ThisInterface interpolateValue(Map<String, ?> args, Closure<Target> postProcess) {
      if (args.containsKey('defaultValueSupplier')) {
        interpolateValue((Context)args['context'], (Supplier<Target>)args.get('defaultValueSupplier', null), (Closure<Boolean>)args.get('ignoreIf', null), postProcess)
      } else {
        interpolateValue((Context)args['context'], (Target)args.get('defaultValue', null), (Closure<Boolean>)args.get('ignoreIf', null), postProcess)
      }
    }

    @Override
    final ThisInterface interpolateValue(Map<String, ?> args) {
      interpolateValue(args, null)
    }

    static {
      ObjectMapperFacade.registerCustomModuleProvider(new SerializerModuleProvider())
    }
  }

  private abstract static class AbstractRaw<
    Source,
    Target extends Serializable,
    ThisInterface extends InterpolableValue<Source, Target, ThisInterface>,
    InterpolatedClass extends Interpolated<Source, Target, ThisInterface, AlreadyInterpolatedClass> & ThisInterface,
    AlreadyInterpolatedClass extends AlreadyInterpolated<Source, Target, ThisInterface>
  > extends Abstract<Source, Target, ThisInterface> implements Cloneable {
    @SuppressWarnings('UnstableApiUsage')
    private static final Constructor<InterpolatedClass> INTERPOLATED_CLASS_CONSTRUCTOR = ((Class<InterpolatedClass>)new TypeToken<InterpolatedClass>(this.class) { }.rawType).getConstructor(AbstractRaw, Context, Object, Closure, Closure)

    @Override
    final ThisInterface interpolateValue(Context context, Supplier<Target> defaultValueSupplier, Closure<Boolean> ignoreIf, Closure<Target> postProcess) {
      INTERPOLATED_CLASS_CONSTRUCTOR.newInstance(this, context, defaultValueSupplier, ignoreIf, postProcess)
    }

    @Override
    final ThisInterface interpolateValue(Context context, Target defaultValue, Closure<Boolean> ignoreIf, Closure<Target> postProcess) {
      INTERPOLATED_CLASS_CONSTRUCTOR.newInstance(this, context, defaultValue, ignoreIf, postProcess)
    }

    @Override
    final Target getInterpolated() {
      throw new ValueNotInterpolatedYetException()
    }

    /*
     * CAVEAT:
     * We use dynamic compiling to run
     * overloaded version of doInterpolatePrimitive
     * depending on raw actual type
     */
    @CompileDynamic
    protected Target doInterpolatePrimitive(Context context) {
      doInterpolatePrimitive context, raw
    }

    protected Target doInterpolatePrimitive(Context context, Object raw) {
      throw new InvalidRawValueClassException(raw)
    }

    @Override
    abstract AbstractRaw<Source, Target, ThisInterface, InterpolatedClass, AlreadyInterpolatedClass> clone()
  }

  protected abstract static class ImmutableRaw<
    Source,
    Target extends Serializable,
    ThisInterface extends InterpolableValue<Source, Target, ThisInterface>,
    InterpolatedClass extends Interpolated<Source, Target, ThisInterface, AlreadyInterpolatedClass> & ThisInterface,
    AlreadyInterpolatedClass extends AlreadyInterpolated<Source, Target, ThisInterface>
  > extends AbstractRaw<Source, Target, ThisInterface, InterpolatedClass, AlreadyInterpolatedClass> {
    private final Source raw

    // This constructor is required for initWithDefault
    ImmutableRaw() {
      this.@raw = null
    }

    // This is public so that it can be simply inherited by implementors // TOTHINK
    @JsonCreator
    ImmutableRaw(Source raw) {
      this.@raw = raw
    }

    @Override
    final Source getRaw() {
      this.@raw
    }

    @Override
    final void setRaw(Source raw) {
      throw new ReadOnlyPropertyException('raw', this.class.canonicalName)
    }

    @Override
    AbstractRaw<Source, Target, ThisInterface, InterpolatedClass, AlreadyInterpolatedClass> clone() {
      this
    }
  }

  protected abstract static class Raw<
    Source,
    Target extends Serializable,
    ThisInterface extends InterpolableValue<Source, Target, ThisInterface>,
    InterpolatedClass extends Interpolated<Source, Target, ThisInterface, AlreadyInterpolatedClass> & ThisInterface,
    AlreadyInterpolatedClass extends AlreadyInterpolated<Source, Target, ThisInterface>
  > extends AbstractRaw<Source, Target, ThisInterface, InterpolatedClass, AlreadyInterpolatedClass> {
    private volatile Source raw

    // This constructor is required for initWithDefault
    Raw() {
      this.@raw = null
    }

    // This is public so that it can be simply inherited by implementors // TOTHINK
    @JsonCreator
    Raw(Source raw) {
      this.@raw = raw
    }

    @Override
    @Synchronized
    final Source getRaw() {
      this.@raw
    }

    @Override
    @Synchronized
    final void setRaw(Source raw) {
      this.@raw = raw
    }

    @Override
    AbstractRaw<Source, Target, ThisInterface, InterpolatedClass, AlreadyInterpolatedClass> clone() {
      Raw<Source, Target, ThisInterface, InterpolatedClass, AlreadyInterpolatedClass> result = (Raw<Source, Target, ThisInterface, InterpolatedClass, AlreadyInterpolatedClass>)this.class.getConstructor().newInstance()
      result.@raw = this.@raw
      result
    }
  }

  // equals is required for Gradle up-to-date checking
  // Doesn't work, don't know why
  // @EqualsAndHashCode(includes = ['interpolated'])
  protected abstract static class AbstractInterpolated<
    Source,
    Target extends Serializable,
    ThisInterface extends InterpolableValue<Source, Target, ThisInterface>
  > extends Abstract<Source, Target, ThisInterface> implements Serializable {
    @Override
    final ThisInterface interpolateValue(Context context, Supplier<Target> defaultValueSupplier, Closure<Boolean> ignoreIf, Closure<Target> postProcess) {
      throw new UnsupportedOperationException('Object is already interpolated')
    }

    @Override
    final ThisInterface interpolateValue(Context context, Target defaultValue, Closure<Boolean> ignoreIf, Closure<Target> postProcess) {
      throw new UnsupportedOperationException('Object is already interpolated')
    }

    @Override
    boolean equals(Object other) {
      if (other == null) return false
      if (this.is(other)) return true
      if (AbstractInterpolated.isInstance(other)) {
        return this.interpolated == ((AbstractInterpolated)other).interpolated
      }
      false
    }
  }

  protected abstract static class Interpolated<
    Source,
    Target extends Serializable,
    ThisInterface extends InterpolableValue<Source, Target, ThisInterface>,
    AlreadyInterpolatedClass extends AlreadyInterpolated<Source, Target, ThisInterface>/*,
    RawClass extends */
  > extends AbstractInterpolated<Source, Target, ThisInterface> {
    @SuppressWarnings('UnstableApiUsage')
    private static final Class<Target> TARGET = (Class<Target>)new TypeToken<Target>(this.class) { }.rawType
    @SuppressWarnings('UnstableApiUsage')
    private static final Class<AlreadyInterpolatedClass> ALREADY_INTERPOLATED_CLASS = (Class<AlreadyInterpolatedClass>)new TypeToken<AlreadyInterpolatedClass>(this.class) { }.rawType

    /*
     * WORKAROUND:
     * We can't have RawValueClass here and later in this class since Groovy doesn't support cyclic generics
     * (at least 2.4.12)
     * TODO: test on 2.5/3.0 and report an issue
     * <grv87 2018-11-22>
     */
    private final /*RawValueClass*/ AbstractRaw interpolable

    private final Context context

    private final Object defaultValue

    private final Closure<Boolean> ignoreIf

    private final Closure<Target> postProcess

    protected Interpolated(/*RawValueClass*/ AbstractRaw interpolable, Context context, Object defaultValue, Closure<Boolean> ignoreIf, Closure<Target> postProcess) {
      this.@interpolable = interpolable.clone()
      this.@context = context
      this.@defaultValue = defaultValue
      this.@ignoreIf = ignoreIf
      this.@postProcess = postProcess
    }

    @Override
    final Source getRaw() {
      (Source)this.@interpolable.raw
    }

    @Override
    @Synchronized
    final void setRaw(Source raw) {
      this.@interpolable.raw = raw
    }

    private final Semaphore interpolationSemaphore = new Semaphore(1)

    private Target getRawDefaultValue() {
      if (Supplier.isInstance(defaultValue)) {
        ((Supplier<Target>)defaultValue).get()
      } else {
        (Target)defaultValue
      }
    }

    /**
     * @serial Interpolated value
     */
    @Override
    @Synchronized
    final Target getInterpolated() {
      if (!interpolationSemaphore.tryAcquire()) {
        throw new RecursiveInterpolationException()
      }
      try {
        if (ignoreIf != null) {
          if (ignoreIf.call() == Boolean.TRUE) {
            return null
          }
        }
        Target result = (Target)interpolable.doInterpolatePrimitive(context)
        if (result != null) {
          if (postProcess) {
            result = (Target)postProcess.call(result)
          }
          return result
        }
        rawDefaultValue
      } finally {
        interpolationSemaphore.release()
      }
    }

    @SuppressWarnings('unused') // IDEA bug
    private static final long serialVersionUID = 6385036190092324496L

    // TOTEST
    Object writeReplace() {
      ALREADY_INTERPOLATED_CLASS.getConstructor(TARGET).newInstance(interpolated)
    }
  }

  protected abstract static class AlreadyInterpolated<
    Source,
    Target extends Serializable,
    ThisInterface extends InterpolableValue<Source, Target, ThisInterface>
  > extends AbstractInterpolated<Source, Target, ThisInterface> {
    // CAVEAT: target should be immutable!
    private Target interpolated

    // This constructor is required for Serializable
    private AlreadyInterpolated(/*RawValueClass*/ ImmutableRaw interpolable, Context context, Supplier<Target> defaultValueSupplier, Closure<Boolean> ignoreIf, Closure<Target> postProcess) {
      this.interpolated = null
    }

    private AlreadyInterpolated(Target interpolated) {
      this.@interpolated = interpolated
    }

    @Override
    final Source getRaw() {
      throw new ObjectAlreadyInterpolatedWithFixedContextException()
    }

    @Override
    final void setRaw(Source raw) {
      throw new ObjectAlreadyInterpolatedWithFixedContextException()
    }

    /**
     * @serial Interpolated value
     */
    @Override
    final Target getInterpolated() {
      this.@interpolated
    }

    @SuppressWarnings('unused') // IDEA bug
    private static final long serialVersionUID = -8039783875049401083L
  }

  private static class CommonExceptions {
    protected static RuntimeException interpolateWithDefault() {
      new UnsupportedOperationException('Value objects should be interpolated with default value and other arguments')
    }
  }

  static final class Serializer extends JsonSerializer<InterpolableValue> {
    private static final Serializer SERIALIZER = new Serializer()

    private Serializer() {}

    @Override
    void serialize(InterpolableValue interpolableValue, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException/*, JsonProcessingException*/ {
      Object raw = interpolableValue.raw
      if (raw != null) {
        jsonGenerator.writeObject(raw)
      }
    }
  }

  private static final class SerializerModuleProvider implements ModuleProvider {
    @Override
    Module getModule(Mutability mutability) {
      SimpleModule serializerModule = new SimpleModule()
      serializerModule.addSerializer(InterpolableValue, Serializer.SERIALIZER)
      serializerModule
    }
  }
}
