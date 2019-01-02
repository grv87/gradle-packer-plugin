package org.fidata.packer.engine

import static com.fasterxml.jackson.databind.PropertyNamingStrategy.PropertyNamingStrategyBase
import static com.fasterxml.jackson.databind.InjectableValues.Std as InjectableValuesStd
import com.google.common.collect.ImmutableMap
import groovy.transform.KnownImmutable
import com.fasterxml.jackson.databind.module.SimpleAbstractTypeResolver
import com.fasterxml.jackson.core.Version
import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.SerializationFeature
import com.google.common.reflect.TypeToken
import org.fidata.version.VersionAdapter
import org.fidata.packer.engine.types.InterpolableDuration
import org.fidata.packer.engine.types.InterpolableFile
import org.fidata.packer.engine.types.InterpolableInteger
import org.fidata.packer.engine.types.InterpolableLong
import org.fidata.packer.engine.types.InterpolableString
import org.fidata.packer.engine.types.InterpolableStringArray
import org.fidata.packer.engine.types.InterpolableURI
import org.fidata.packer.engine.types.InterpolableUnsignedInteger
import org.fidata.packer.engine.types.base.InterpolableValue
import java.lang.reflect.Constructor
import com.google.common.collect.ImmutableList
import org.fidata.packer.engine.types.base.InterpolableObject
import org.fidata.packer.engine.types.InterpolableBoolean
import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.core.JsonEncoding
import com.fasterxml.jackson.core.JsonGenerationException
import com.fasterxml.jackson.core.JsonParseException
import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.core.JsonProcessingException
import com.fasterxml.jackson.databind.JsonMappingException
import com.fasterxml.jackson.databind.Module
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.paramnames.ParameterNamesModule
import com.fasterxml.jackson.datatype.guava.GuavaModule
import com.fasterxml.jackson.databind.PropertyNamingStrategy
import com.fasterxml.jackson.module.afterburner.AfterburnerModule
import groovy.transform.CompileStatic
// import groovy.transform.Synchronized

/**
 * This class acts as {@link ObjectMapper} facade
 * providing only required methods
 * and hiding any configuration options.
 *
 * This class should be extended, not instantiated directly
 */
@CompileStatic
abstract class AbstractEngine<T extends InterpolableObject<T>> {
  @SuppressWarnings('UnstableApiUsage')
  private final Class<T> tClass = new TypeToken<T>(this.class) { }.rawType

  public static final PropertyNamingStrategyBase PROPERTY_NAMING_STRATEGY = (PropertyNamingStrategyBase)PropertyNamingStrategy.SNAKE_CASE

  private final Mutability mutability

  protected AbstractEngine(Mutability mutability) {
    this.@mutability = mutability
  }

  private final AbstractTypeMappingRegistry abstractTypeMappingRegistry = new AbstractTypeMappingRegistry()

  AbstractTypeMappingRegistry getAbstractTypeMappingRegistry() {
    this.@abstractTypeMappingRegistry
  }

  private final Map<Class<? extends InterpolableObject>, SubtypeRegistry> subtypeRegistries = [:]

  /*
   * WORKAROUND:
   * Without public we got compilation error:
   * unexpected token: <
   * Groovy bug
   * <grv87 2018-12-30>
   */
  public <BaseType extends InterpolableObject<BaseType>> SubtypeRegistry<BaseType> addSubtypeRegistry(Class<BaseType> baseType) {
    SubtypeRegistry<BaseType> subtypeRegistry = SubtypeRegistry.forType(baseType)
    subtypeRegistries[baseType] = subtypeRegistry
    subtypeRegistry
  }

  /*
   * WORKAROUND:
   * Without public we got compilation error:
   * unexpected token: <
   * Groovy bug
   * <grv87 2018-12-30>
   */
  public <BaseType extends InterpolableObject<BaseType>> void registerSubtype(Class<BaseType> baseType, String name, Class<? extends BaseType> clazz) {
    subtypeRegistries[baseType].registerSubtype name, clazz
  }

  // @Synchronized
  public <T extends InterpolableObject<T>> T instantiate(Class<T> abstractClass, Mutability mutability) {
    abstractTypeMappingRegistry.instantiate(abstractClass, mutability)
  }

  // @Synchronized
  public <T extends InterpolableObject<T>> T instantiate(Class<T> baseClass, String name, Mutability mutability) {
    instantiate(subtypeRegistries[baseClass][name], mutability)
  }

  private static final List<Module> DEFAULT_MODULES = ImmutableList.of(
    (Module)new ParameterNamesModule(JsonCreator.Mode.PROPERTIES),
    (Module)new GuavaModule(),
    (Module)new AfterburnerModule(),
    (Module)InterpolableValue.Serializer.SERIALIZER_MODULE
  )

  /*
   * CAVEAT:
   * Lazy initialization to avoid
   * escaping `this` from constructor
   */
  @Lazy
  private InjectableValuesStd injectableValues = {
    InjectableValuesStd injectableValues = new InjectableValuesStd()
    injectableValues.addValue AbstractEngine, this
    injectableValues
  }()

  @Lazy
  private ObjectMapper objectMapper = {
    ObjectMapper objectMapper = new ObjectMapper()

    // CAVEAT: This could be better done in InterpolableEnum, but it can't be done from Module
    objectMapper.enable(DeserializationFeature.READ_ENUMS_USING_TO_STRING)
    objectMapper.enable(SerializationFeature.WRITE_ENUMS_USING_TO_STRING)

    objectMapper.serializationInclusion = JsonInclude.Include.NON_NULL
    objectMapper.propertyNamingStrategy = PROPERTY_NAMING_STRATEGY

    objectMapper.registerModules DEFAULT_MODULES
    objectMapper.registerModule abstractTypeMappingRegistry
    objectMapper.registerModules subtypeRegistries.values()

    objectMapper.injectableValues = injectableValues // TODO

    objectMapper
  }()

  @KnownImmutable
  final static class AbstractTypeMapping {
    final boolean noArgConstructor
    final Map<Mutability, Class<? extends InterpolableObject>> implementations
    @Lazy
    Map<Mutability, Constructor<? extends InterpolableObject>> constructors = ImmutableMap.copyOf(implementations.collectEntries { Mutability mutability, Class<? extends InterpolableObject> implementation ->
      // CAVEAT: Using public constructors only
      [(mutability): noArgConstructor ? implementation.getConstructor() : implementation.getConstructor(AbstractEngine)]
    })
    AbstractTypeMapping(boolean noArgConstructor, Map<Mutability, Class<? extends InterpolableObject>> implementations) {
      this.@noArgConstructor = noArgConstructor
      this.@implementations = ImmutableMap.copyOf(implementations)
    }
    AbstractTypeMapping(boolean noArgConstructor, Class<? extends InterpolableObject> mutableImplementation, Class<? extends InterpolableObject> immutableImplementation) {
      this.@noArgConstructor = noArgConstructor
      this.@implementations = ImmutableMap.of(
        Mutability.MUTABLE, mutableImplementation,
        Mutability.IMMUTABLE, immutableImplementation,
      )
    }
  }

  final class AbstractTypeMappingRegistry extends Module {
    private final Map<Class<? extends InterpolableObject>, AbstractTypeMapping> registry = [:]

    AbstractTypeMappingRegistry() {
      // InterpolableValue descendants don't have built-in register methods

      // Strings
      registerAbstractTypeMapping InterpolableString, true, InterpolableString.Raw, InterpolableString.ImmutableRaw
      registerAbstractTypeMapping InterpolableStringArray, true, InterpolableStringArray.Raw, InterpolableStringArray.ImmutableRaw

      // Numeric
      registerAbstractTypeMapping InterpolableInteger, true, InterpolableInteger.Raw, InterpolableInteger.ImmutableRaw
      registerAbstractTypeMapping InterpolableLong, true, InterpolableLong.Raw, InterpolableLong.ImmutableRaw
      registerAbstractTypeMapping InterpolableUnsignedInteger, true, InterpolableUnsignedInteger.Raw, InterpolableUnsignedInteger.ImmutableRaw

      // Boolean
      registerAbstractTypeMapping InterpolableBoolean, true, InterpolableBoolean.Raw, InterpolableBoolean.ImmutableRaw

      // File & URI
      registerAbstractTypeMapping InterpolableFile, true, InterpolableFile.Raw, InterpolableFile.ImmutableRaw
      registerAbstractTypeMapping InterpolableURI, true, InterpolableURI.Raw, InterpolableURI.ImmutableRaw

      // Miscellaneous
      registerAbstractTypeMapping InterpolableDuration, true, InterpolableDuration.Raw, InterpolableDuration.ImmutableRaw
    }

    // @Synchronized
    public <T extends InterpolableObject<T>> void registerAbstractTypeMapping(Class<? extends T> abstractClass, boolean noArgConstructor = false, Class<? extends T> mutableImplementation, Class<? extends T> immutableImplementation) {
      if (registry.containsKey(abstractClass)) {
        throw new IllegalArgumentException(sprintf('Abstract type mapping for type %s is already registered', [abstractClass.canonicalName]))
      }
      // TODO: Check that implementations are not generic classes ?
      registry[abstractClass] = new AbstractTypeMapping(noArgConstructor, mutableImplementation, immutableImplementation)
    }

    // @Synchronized
    public <T extends InterpolableObject<T>> T instantiate(Class<T> abstractClass, Mutability mutability) {
      AbstractTypeMapping abstractTypeMapping = registry[abstractClass]
      Constructor<T> constructor = abstractTypeMapping.constructors[mutability]
      if (abstractTypeMapping.noArgConstructor) {
        (T)constructor.newInstance()
      } else {
        (T)constructor.newInstance(AbstractEngine.this)
      }
    }

    @Override
    String getModuleName() {
      return this.class.canonicalName
    }

    @Lazy
    private VersionAdapter version = VersionAdapter.mavenVersionFor(this.class.classLoader, 'org.fidata', 'gradle-packer-plugin')

    @Override
    Version version() {
      this.@version.asJackson()
    }

    @Override
    void setupModule(SetupContext context) {
      /*
       * WORKAROUND:
       * We have to instantiate SimpleAbstractTypeResolver
       * since Jackson API don't use interfaces.
       * See https://github.com/FasterXML/jackson-databind/issues/2214
       * <grv87 2018-12-30>
       */
      SimpleAbstractTypeResolver abstractTypeResolver = new SimpleAbstractTypeResolver()
      registry.each { Class<? extends InterpolableObject> key, AbstractTypeMapping abstractTypeMapping ->
        abstractTypeResolver.addMapping key, abstractTypeMapping.implementations[AbstractEngine.this.mutability]
      }
      context.addAbstractTypeResolver abstractTypeResolver
    }
  }

  /**********************************************************/
  /* Facade for ObjectMapper methods
  /**********************************************************/

  /**
   * Method to deserialize JSON content from given file into given Java type.
   *
   * @throws IOException if a low-level I/O problem (unexpected end-of-input,
   *   network error) occurs (passed through as-is without additional wrapping -- note
   *   that this is one case where {@link com.fasterxml.jackson.databind.DeserializationFeature#WRAP_EXCEPTIONS}
   *   does NOT result in wrapping of exception even if enabled)
   * @throws com.fasterxml.jackson.core.JsonParseException if underlying input contains invalid content
   *    of type {@link JsonParser} supports (JSON for default case)
   * @throws com.fasterxml.jackson.databind.JsonMappingException if the input JSON structure does not match structure
   *   expected for result type (or has other mismatch issues)
   */
  @SuppressWarnings('unchecked')
  T readValue(File src) throws IOException, JsonParseException, JsonMappingException {
    objectMapper.readValue(src, tClass)
  }

  /**
   * Method to deserialize JSON content from given resource into given Java type.
   *
   * @throws IOException if a low-level I/O problem (unexpected end-of-input,
   *   network error) occurs (passed through as-is without additional wrapping -- note
   *   that this is one case where {@link com.fasterxml.jackson.databind.DeserializationFeature#WRAP_EXCEPTIONS}
   *   does NOT result in wrapping of exception even if enabled)
   * @throws JsonParseException if underlying input contains invalid content
   *    of type {@link JsonParser} supports (JSON for default case)
   * @throws JsonMappingException if the input JSON structure does not match structure
   *   expected for result type (or has other mismatch issues)
   */
  @SuppressWarnings('unchecked')
  T readValue(URL src) throws IOException, JsonParseException, JsonMappingException {
    objectMapper.readValue(src, tClass)
  }

  /**
   * Method to deserialize JSON content from given JSON content String.
   *
   * @throws IOException if a low-level I/O problem (unexpected end-of-input,
   *   network error) occurs (passed through as-is without additional wrapping -- note
   *   that this is one case where {@link com.fasterxml.jackson.databind.DeserializationFeature#WRAP_EXCEPTIONS}
   *   does NOT result in wrapping of exception even if enabled)
   * @throws JsonParseException if underlying input contains invalid content
   *    of type {@link JsonParser} supports (JSON for default case)
   * @throws JsonMappingException if the input JSON structure does not match structure
   *   expected for result type (or has other mismatch issues)
   */
  @SuppressWarnings('unchecked')
  T readValue(String content) throws IOException, JsonParseException, JsonMappingException {
    objectMapper.readValue(content, tClass)
  }

  @SuppressWarnings('unchecked')
  T readValue(Reader src) throws IOException, JsonParseException, JsonMappingException {
    objectMapper.readValue(src, tClass)
  }

  @SuppressWarnings('unchecked')
  T readValue(InputStream src) throws IOException, JsonParseException, JsonMappingException {
    objectMapper.readValue(src, tClass)
  }

  /**
   * Method that can be used to serialize any Java value as
   * JSON output, written to File provided.
   */
  void writeValue(File resultFile, T value) throws IOException, JsonGenerationException, JsonMappingException {
    objectMapper.writeValue resultFile, value
  }

  /**
   * Method that can be used to serialize any Java value as
   * JSON output, using output stream provided (using encoding
   * {@link JsonEncoding#UTF8}).
   * <p>
   * Note: method does not close the underlying stream explicitly
   * here; however, {@link com.fasterxml.jackson.core.JsonFactory} this mapper uses may choose
   * to close the stream depending on its settings (by default,
   * it will try to close it when {@link com.fasterxml.jackson.core.JsonGenerator} we construct
   * is closed).
   */
  void writeValue(OutputStream out, T value) throws IOException, JsonGenerationException, JsonMappingException {
    objectMapper.writeValue out, value
  }

  /**
   * Method that can be used to serialize any Java value as
   * JSON output, using Writer provided.
   * <p>
   * Note: method does not close the underlying stream explicitly
   * here; however, {@link com.fasterxml.jackson.core.JsonFactory} this mapper uses may choose
   * to close the stream depending on its settings (by default,
   * it will try to close it when {@link com.fasterxml.jackson.core.JsonGenerator} we construct
   * is closed).
   */
  void writeValue(Writer w, T value) throws IOException, JsonGenerationException, JsonMappingException {
    objectMapper.writeValue w, value
  }

  /**
   * Method that can be used to serialize any Java value as
   * a String. Functionally equivalent to calling
   * {@link #writeValue(Writer, Object)} with {@link java.io.StringWriter}
   * and constructing String, but more efficient.
   * <p>
   * Note: prior to version 2.1, throws clause included {@link IOException}; 2.1 removed it.
   */
  String writeValueAsString(Object value) throws JsonProcessingException {
    objectMapper.writeValueAsString(value)
  }
}
