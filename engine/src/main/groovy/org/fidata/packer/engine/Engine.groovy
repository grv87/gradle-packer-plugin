package org.fidata.packer.engine

import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.databind.jsontype.NamedType
import com.google.common.reflect.TypeToken

import static com.fasterxml.jackson.databind.PropertyNamingStrategy.PropertyNamingStrategyBase
import static com.fasterxml.jackson.databind.InjectableValues.Std as InjectableValuesStd
import org.fidata.packer.engine.types.InterpolableDuration
import org.fidata.packer.engine.types.InterpolableFile
import org.fidata.packer.engine.types.InterpolableInputDirectory
import org.fidata.packer.engine.types.InterpolableInputURI
import org.fidata.packer.engine.types.InterpolableInteger
import org.fidata.packer.engine.types.InterpolableLong
import org.fidata.packer.engine.types.InterpolableString
import org.fidata.packer.engine.types.InterpolableStringArray
import org.fidata.packer.engine.types.InterpolableURI
import org.fidata.packer.engine.types.InterpolableUnsignedInteger
import org.fidata.packer.engine.types.base.InterpolableValue

import java.lang.reflect.Constructor
import com.google.common.collect.ImmutableList
import com.google.common.collect.ImmutableMap
import com.fasterxml.jackson.databind.module.SimpleModule
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
import groovy.transform.Synchronized

/*
 * This class acts as ObjectMapper facade
 * providing only required methods
 * and hiding any configuration options
 */
@CompileStatic
final class Engine<T> {
  @SuppressWarnings('UnstableApiUsage')
  private final Class<T> tClass = new TypeToken<T>(this.class) { }.rawType

  public static final PropertyNamingStrategyBase PROPERTY_NAMING_STRATEGY = (PropertyNamingStrategyBase)PropertyNamingStrategy.SNAKE_CASE

  @Lazy
  private Map<Mutability, ObjectMapper> objectMappers = ImmutableMap.copyOf(
    Mutability.values().collectEntries { Mutability mutability ->
      [(mutability): new ObjectMapper()]
    }
  )

  private final AbstractTypeMappingRegistry abstractTypeMappingRegistry = new AbstractTypeMappingRegistry()

  AbstractTypeMappingRegistry getAbstractTypeMappingRegistry() {
    this.@abstractTypeMappingRegistry
  }

  Engine() {
    addSubtypeRegistry abstractTypeMappingRegistry
    addSubtypeRegistry InterpolableValue.Serializer.SERIALIZER_MODULE
  }

  @Synchronized
  void addSubtypeRegistry(SubtypeRegistry<? extends InterpolableObject> subtypeRegistry) {
    customModuleProviderRegistry.add subtypeRegistry
  }

  // @KnownImmutable
  final static class AbstractTypeMapping {
    final boolean noArgConstructor
    final Map<Mutability, Class<? extends InterpolableObject>> implementations
    final Map<Mutability, Constructor<? extends InterpolableObject>> constructors
    AbstractTypeMapping(boolean noArgConstructor, Map<Mutability, Class<? extends InterpolableObject>> implementations) {
      this.@noArgConstructor = noArgConstructor
      this.@implementations = ImmutableMap.copyOf(implementations)
      this.@constructors = ImmutableMap.copyOf(implementations.collectEntries { Mutability key, Class<? extends InterpolableObject> implementation ->
        // CAVEAT: Using public constructors only
        [(key): noArgConstructor ? implementation.getConstructor() : implementation.getConstructor(Engine)]
      })
    }
  }

  final class AbstractTypeMappingRegistry extends Module {
    private final Map<Class<? extends InterpolableObject>, AbstractTypeMapping> registry = [:]
    private final Map<Mutability, SimpleModule> modules = [:]

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
      registerAbstractTypeMapping InterpolableInputDirectory, true, InterpolableInputDirectory.Raw, InterpolableInputDirectory.ImmutableRaw
      registerAbstractTypeMapping InterpolableURI, true, InterpolableURI.Raw, InterpolableURI.ImmutableRaw
      registerAbstractTypeMapping InterpolableInputURI, true, InterpolableInputURI.Raw, InterpolableInputURI.ImmutableRaw

      // Miscellaneous
      registerAbstractTypeMapping InterpolableDuration, true, InterpolableDuration.Raw, InterpolableDuration.ImmutableRaw
    }

    @Synchronized
    <T extends InterpolableObject<T>> void registerAbstractTypeMapping(Class<? extends T> abstractClass, boolean noArgConstructor = false, Class<? extends T> mutableClass, Class<? extends T> immutableClass) {
      if (registry.containsKey(abstractClass)) {
        throw new IllegalArgumentException(sprintf('Abstract type mapping for type %s is already registered', [abstractClass.canonicalName]))
      }
      modules.clear()
      registry[abstractClass] = new AbstractTypeMapping(noArgConstructor, [
        (Mutability.MUTABLE): mutableClass,
        (Mutability.IMMUTABLE): immutableClass
      ])
    }

    @Synchronized
    <T extends InterpolableObject<T>> T instantiate(Class<T> abstractClass, Mutability mutability) {
      AbstractTypeMapping abstractTypeMapping = registry[abstractClass]
      if (abstractTypeMapping.noArgConstructor) {
        (T)abstractTypeMapping.constructors[mutability].newInstance()
      } else {
        (T)abstractTypeMapping.constructors[mutability].newInstance(Engine.this)
      }
    }

    @Override
    String getModuleName() {
      return null
    }

    @Override
    Version version() {
      return null
    }

    @Override
    void setupModule(SetupContext context) {
      registry.each { Class<? extends InterpolableObject> key, AbstractTypeMapping abstractTypeMapping ->
        context.addAbstractTypeMapping key, abstractTypeMapping.implementations[mutability]
      }

      subtypeRegistry.each { String key, Class<? extends T> value ->
        context.registerSubtypes(new NamedType(value, key))
      }
    }
  }

  /*
   * CAVEAT:
   * We assume here and in ModuleProvider that nobody
   * changes configuration of ObjectMapper's modules etc.
   * We hide ObjectMapper behind Facade, but it is still possible
   * to do something hazardous in custom deserializers etc.
   * It's up to plugin authors to meet these expectations
   */
  private static final List<Module> DEFAULT_MODULES = ImmutableList.of(
    (Module)new ParameterNamesModule(JsonCreator.Mode.PROPERTIES),
    (Module)new GuavaModule(),
    (Module)new AfterburnerModule(),
  )

  /*
   * CAVEAT:
   * Lazy initialization to avoid
   * escaping `this` from constructor
   */
  @Lazy
  private InjectableValuesStd injectableValues = {
    InjectableValuesStd injectableValues = new InjectableValuesStd()
    injectableValues.addValue Engine, this
    injectableValues
  }()

  @Synchronized
  private ObjectMapper getObjectMapper(Mutability mutability) {
    ObjectMapperFacade facade = objectMappers[mutability]
    Set<Module> customModules = customModuleProviderRegistry*.getModule(mutability).toSet()
    if (facade != null && facade.@customModules == customModules) {
      return facade
    }
    ObjectMapper objectMapper = new ObjectMapper()
    objectMapper.enable(DeserializationFeature.READ_ENUMS_USING_TO_STRING)
    objectMapper.enable(SerializationFeature.WRITE_ENUMS_USING_TO_STRING)
    objectMapper.serializationInclusion = JsonInclude.Include.NON_NULL
    objectMapper.propertyNamingStrategy = PROPERTY_NAMING_STRATEGY
    objectMapper.registerModules DEFAULT_MODULES
    objectMapper.injectableValues = injectableValues

    facade = new ObjectMapperFacade(objectMapper, customModules)
    objectMappers[mutability] = facade
    facade
  }

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
  T readValue(File src, ) throws IOException, JsonParseException, JsonMappingException {
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
