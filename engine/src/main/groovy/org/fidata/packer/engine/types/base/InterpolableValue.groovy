package org.fidata.packer.engine.types.base

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.core.Version
import com.fasterxml.jackson.databind.Module
import com.fasterxml.jackson.databind.module.SimpleSerializers
import com.fasterxml.jackson.databind.ser.Serializers
import org.fidata.packer.engine.exceptions.InvalidRawValueClassException
import org.fidata.packer.engine.exceptions.ObjectAlreadyInterpolatedWithFixedContextException
import org.fidata.packer.engine.exceptions.RecursiveInterpolationException
import org.fidata.packer.engine.exceptions.ValueNotInterpolatedYetException
import com.github.hashicorp.packer.template.Context
import com.google.common.base.Supplier
import com.google.common.reflect.TypeToken
import groovy.transform.CompileStatic
import groovy.transform.CompileDynamic
import groovy.transform.KnownImmutable

// import groovy.transform.EqualsAndHashCode
import groovy.transform.Synchronized
import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.databind.JsonSerializer
import com.fasterxml.jackson.databind.SerializerProvider
import groovy.transform.stc.ClosureParams
import groovy.transform.stc.FromString
import org.apache.commons.lang3.BooleanUtils

import java.lang.reflect.Constructor
import java.util.concurrent.Callable
import java.util.concurrent.Semaphore

// @AutoExternalize(excludes = ['raw']) // TODO: Groovy 2.5.0
@CompileStatic
// Serializable is required for Gradle up-to-date checking
interface InterpolableValue<
  Source,
  Target extends Serializable,
  ThisInterface extends InterpolableValue<Source, Target, ThisInterface>
> extends InterpolableObject<ThisInterface> {
  /**
   *
   * @throws ObjectAlreadyInterpolatedWithFixedContextException
   * @return
   */
  Source getRaw()

  /**
   *
   * @param raw
   * @throws ReadOnlyPropertyException if this instance is read-only
   * @throws ObjectAlreadyInterpolatedWithFixedContextException
   */
  void setRaw(Source raw)

  ThisInterface interpolateValue(Context context, Supplier<Target> defaultValueSupplier, Callable<Boolean> ignoreIf, @ClosureParams(value = FromString, options = ['Target']) Closure<Target> postProcess)

  ThisInterface interpolateValue(Context context, Target defaultValue, Callable<Boolean> ignoreIf, @ClosureParams(value = FromString, options = ['Target']) Closure<Target> postProcess)

  ThisInterface interpolateValue(Context context, Supplier<Target> defaultValueSupplier, Callable<Boolean> ignoreIf)

  ThisInterface interpolateValue(Context context, Target defaultValue, Callable<Boolean> ignoreIf)

  ThisInterface interpolateValue(Context context, Supplier<Target> defaultValueSupplier)

  ThisInterface interpolateValue(Context context, Target defaultValue)

  ThisInterface interpolateValue(Context context)

  // Note: Map variants are provided but not used, since it is unnecessary map creation and dissection
  ThisInterface interpolateValue(Map<String, ?> args, @ClosureParams(value = FromString, options = ['Target']) Closure<Target> postProcess)

  ThisInterface interpolateValue(Map<String, ?> args)

  Target getInterpolated()

  private abstract static class Abstract<
    Source,
    Target extends Serializable,
    ThisInterface extends InterpolableValue<Source, Target, ThisInterface>
  > implements InterpolableValue<Source, Target, ThisInterface> {
    // TODO: With final error: You are not allowed to override the final method interpolate(com.github.hashicorp.packer.template.Context -> com.github.hashicorp.packer.template.Context) from class 'org.fidata.packer.engine.types.base.InterpolableValue$Abstract'.
    @Override
    /*final*/ ThisInterface interpolate(Context context) {
      throw CommonExceptions.interpolateWithDefault()
    }

    @Override
    final ThisInterface interpolateValue(Context context, Supplier<Target> defaultValueSupplier, Callable<Boolean> ignoreIf) {
      interpolateValue(context, defaultValueSupplier, ignoreIf, null)
    }

    @Override
    final ThisInterface interpolateValue(Context context, Target defaultValue, Callable<Boolean> ignoreIf) {
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
    final ThisInterface interpolateValue(Map<String, ?> args, @ClosureParams(value = FromString, options = ['Target']) Closure<Target> postProcess) {
      if (args.containsKey('defaultValueSupplier')) {
        interpolateValue((Context)args['context'], (Supplier<Target>)args.get('defaultValueSupplier', null), (Callable<Boolean>)args.get('ignoreIf', null), postProcess)
      } else {
        interpolateValue((Context)args['context'], (Target)args.get('defaultValue', null), (Callable<Boolean>)args.get('ignoreIf', null), postProcess)
      }
    }

    @Override
    final ThisInterface interpolateValue(Map<String, ?> args) {
      interpolateValue(args, null)
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
    private final Constructor<InterpolatedClass> interpolatedClassConstructor = ((Class<InterpolatedClass>)new TypeToken<InterpolatedClass>(this.class) { }.rawType).getConstructor(AbstractRaw, Context, Object, Callable, Closure)

    @Override
    final ThisInterface interpolateValue(Context context, Supplier<Target> defaultValueSupplier, Callable<Boolean> ignoreIf, @ClosureParams(value = FromString, options = ['Target']) Closure<Target> postProcess) {
      interpolatedClassConstructor.newInstance(this, context, defaultValueSupplier, ignoreIf, postProcess)
    }

    @Override
    final ThisInterface interpolateValue(Context context, Target defaultValue, Callable<Boolean> ignoreIf, @ClosureParams(value = FromString, options = ['Target']) Closure<Target> postProcess) {
      interpolatedClassConstructor.newInstance(this, context, defaultValue, ignoreIf, postProcess)
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

  @KnownImmutable
  abstract static class ImmutableRaw<
    Source,
    Target extends Serializable,
    ThisInterface extends InterpolableValue<Source, Target, ThisInterface>,
    InterpolatedClass extends Interpolated<Source, Target, ThisInterface, AlreadyInterpolatedClass> & ThisInterface,
    AlreadyInterpolatedClass extends AlreadyInterpolated<Source, Target, ThisInterface>
  > extends AbstractRaw<Source, Target, ThisInterface, InterpolatedClass, AlreadyInterpolatedClass> {
    private final Source raw

    // This constructor is required for AbstractTypeMappingRegistry.instantiate
    ImmutableRaw() {
      this.@raw = null
    }

    // This is public so that it can be simply inherited by implementors // TOTHINK
    @JsonCreator(mode = JsonCreator.Mode.DELEGATING)
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

  abstract static class Raw<
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
    @JsonCreator(mode = JsonCreator.Mode.DELEGATING)
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
  private abstract static class AbstractInterpolated<
    Source,
    Target extends Serializable,
    ThisInterface extends InterpolableValue<Source, Target, ThisInterface>
  > extends Abstract<Source, Target, ThisInterface> implements Serializable {
    @Override
    final ThisInterface interpolateValue(Context context, Supplier<Target> defaultValueSupplier, Callable<Boolean> ignoreIf, @ClosureParams(value = FromString, options = ['Target']) Closure<Target> postProcess) {
      throw new UnsupportedOperationException('Object is already interpolated')
    }

    @Override
    final ThisInterface interpolateValue(Context context, Target defaultValue, Callable<Boolean> ignoreIf, @ClosureParams(value = FromString, options = ['Target']) Closure<Target> postProcess) {
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

    @Override
    int hashCode() {
      interpolated.hashCode()
    }
  }

  abstract static class Interpolated<
    Source,
    Target extends Serializable,
    ThisInterface extends InterpolableValue<Source, Target, ThisInterface>,
    AlreadyInterpolatedClass extends AlreadyInterpolated<Source, Target, ThisInterface>/*,
    RawClass extends */
  > extends AbstractInterpolated<Source, Target, ThisInterface> {
    @SuppressWarnings('UnstableApiUsage')
    private final Class<Target> target = (Class<Target>)new TypeToken<Target>(this.class) { }.rawType
    @SuppressWarnings('UnstableApiUsage')
    private final Class<AlreadyInterpolatedClass> alreadyInterpolatedClass = (Class<AlreadyInterpolatedClass>)new TypeToken<AlreadyInterpolatedClass>(this.class) { }.rawType

    /*
     * WORKAROUND:
     * We can't have RawValueClass here and later in this class since Groovy doesn't support cyclic generics
     * (at least 2.4.12)
     * TODO: test on 2.5/3.0 and report an issue
     * <grv87 2018-11-22>
     */
    private final /*RawValueClass*/ AbstractRaw raw

    private final Context context

    private final Object defaultValue

    private final Callable<Boolean> ignoreIf

    private final Closure<Target> postProcess

    /*
     * CAVEAT:
     * This constructor is made public, so that AbstractRaw can get this constructor with reflection
     * without any specific permissions.
     * Maybe this would not be a problem with PackageScope or even private access,
     * but we don't want to risk
     */
    Interpolated(/*RawValueClass*/ AbstractRaw raw, Context context, Object defaultValue, Callable<Boolean> ignoreIf, @ClosureParams(value = FromString, options = ['Target']) Closure<Target> postProcess) {
      this.@raw = raw.clone()
      this.@context = context
      this.@defaultValue = defaultValue
      this.@ignoreIf = ignoreIf
      this.@postProcess = postProcess
    }

    @Override
    final Source getRaw() {
      (Source)this.@raw.raw
    }

    @Override
    @Synchronized
    final void setRaw(Source raw) {
      this.@raw.raw = raw
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
          if (BooleanUtils.isTrue(ignoreIf.call())) {
            return null
          }
        }
        Target result = (Target)raw.doInterpolatePrimitive(context)
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
      alreadyInterpolatedClass.getConstructor(target).newInstance(interpolated)
    }
  }

  @KnownImmutable
  abstract static class AlreadyInterpolated<
    Source,
    Target extends Serializable,
    ThisInterface extends InterpolableValue<Source, Target, ThisInterface>
  > extends AbstractInterpolated<Source, Target, ThisInterface> {
    // CAVEAT: target should be immutable!
    private Target interpolated

    AlreadyInterpolated(Target interpolated) {
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
    protected static final Serializer SERIALIZER = new Serializer()

    private Serializer() {}

    @Override
    void serialize(InterpolableValue interpolableValue, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException/*, JsonProcessingException*/ {
      Object raw = interpolableValue.raw
      if (raw != null) {
        jsonGenerator.writeObject(raw)
      }
    }

    static final Module SERIALIZER_MODULE = new SerializerModule()

    private static final class SerializerModule extends Module {
      @Override
      String getModuleName() {
        return null // TODO
      }

      @Override
      Version version() {
        return null // TODO
      }

      @Override
      void setupModule(SetupContext context) {
        Serializers serializers = new SimpleSerializers()
        serializers.addSerializer InterpolableValue, SERIALIZER
        context.addSerializers serializers
      }
    }
  }
}
