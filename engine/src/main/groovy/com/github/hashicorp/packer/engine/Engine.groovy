package com.github.hashicorp.packer.engine

import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.SerializationFeature

import static com.fasterxml.jackson.databind.PropertyNamingStrategy.PropertyNamingStrategyBase
import static com.fasterxml.jackson.databind.InjectableValues.Std as InjectableValuesStd
import com.github.hashicorp.packer.engine.types.InterpolableDuration
import com.github.hashicorp.packer.engine.types.InterpolableFile
import com.github.hashicorp.packer.engine.types.InterpolableInputDirectory
import com.github.hashicorp.packer.engine.types.InterpolableInputURI
import com.github.hashicorp.packer.engine.types.InterpolableInteger
import com.github.hashicorp.packer.engine.types.InterpolableLong
import com.github.hashicorp.packer.engine.types.InterpolableString
import com.github.hashicorp.packer.engine.types.InterpolableStringArray
import com.github.hashicorp.packer.engine.types.InterpolableURI
import com.github.hashicorp.packer.engine.types.InterpolableUnsignedInteger
import com.github.hashicorp.packer.engine.types.base.InterpolableValue
import groovy.transform.EqualsAndHashCode
import groovy.transform.ImmutableBase
import groovy.transform.KnownImmutable
import groovy.transform.ToString
import java.lang.reflect.Constructor
import com.google.common.collect.ImmutableList
import com.google.common.collect.ImmutableMap
import groovy.transform.Immutable
import com.fasterxml.jackson.databind.module.SimpleModule
import com.github.hashicorp.packer.engine.types.base.InterpolableObject
import com.github.hashicorp.packer.engine.types.InterpolableBoolean
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

@CompileStatic
final class Engine {
  public static final PropertyNamingStrategyBase PROPERTY_NAMING_STRATEGY = (PropertyNamingStrategyBase)PropertyNamingStrategy.SNAKE_CASE

  private final Set<ModuleProvider> customModuleProviderRegistry = new HashSet()
  private Map<Mutability, ObjectMapperFacade> facades = new HashMap<>(2)

  private final AbstractTypeMappingRegistry abstractTypeMappingRegistry = new AbstractTypeMappingRegistry()

  AbstractTypeMappingRegistry getAbstractTypeMappingRegistry() {
    this.@abstractTypeMappingRegistry
  }

  Engine() {
    registerCustomModuleProvider abstractTypeMappingRegistry
    registerCustomModuleProvider InterpolableValue.Serializer.MODULE_PROVIDER
  }

  @Synchronized
  void registerCustomModuleProvider(ModuleProvider moduleProvider) {
    facades.clear()
    customModuleProviderRegistry.add moduleProvider
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

  final class AbstractTypeMappingRegistry implements ModuleProvider {
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

    @Override
    @Synchronized
    Module getModule(Mutability mutability) {
      SimpleModule module = modules[mutability]
      if (module == null) {
        module = new SimpleModule()
        registry.each { Class<? extends InterpolableObject> key, AbstractTypeMapping abstractTypeMapping ->
          module.addAbstractTypeMapping key, abstractTypeMapping.implementations[mutability]
        }
        modules[mutability] = module
      }
      module
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
  final ObjectMapperFacade getObjectMapperFacade(Mutability mutability) {
    ObjectMapperFacade facade = facades[mutability]
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
    facades[mutability] = facade
    facade
  }

  final static class ObjectMapperFacade {
    private final ObjectMapper objectMapper
    private final Set<Module> customModules

    private ObjectMapperFacade(ObjectMapper objectMapper, Set<Module> customModules) {
      this.@objectMapper = objectMapper
      this.@customModules = customModules
      objectMapper.registerModules customModules
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
    <T> T readValue(File src, Class<T> valueType) throws IOException, JsonParseException, JsonMappingException {
      objectMapper.readValue(src, valueType)
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
    <T> T readValue(URL src, Class<T> valueType) throws IOException, JsonParseException, JsonMappingException {
      objectMapper.readValue(src, valueType)
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
    <T> T readValue(String content, Class<T> valueType) throws IOException, JsonParseException, JsonMappingException {
      objectMapper.readValue(content, valueType)
    }

    @SuppressWarnings('unchecked')
    <T> T readValue(Reader src, Class<T> valueType) throws IOException, JsonParseException, JsonMappingException {
      objectMapper.readValue(src, valueType)
    }

    @SuppressWarnings('unchecked')
    <T> T readValue(InputStream src, Class<T> valueType) throws IOException, JsonParseException, JsonMappingException {
      objectMapper.readValue(src, valueType)
    }

    /**
     * Method that can be used to serialize any Java value as
     * JSON output, written to File provided.
     */
    void writeValue(File resultFile, Object value) throws IOException, JsonGenerationException, JsonMappingException {
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
    void writeValue(OutputStream out, Object value) throws IOException, JsonGenerationException, JsonMappingException {
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
    void writeValue(Writer w, Object value) throws IOException, JsonGenerationException, JsonMappingException {
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
}
