package com.github.hashicorp.packer.engine.utils

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
final class ObjectMapperFacade {
  private static final Set<ModuleProvider> CUSTOM_MODULE_PROVIDER_REGISTRY = new HashSet()
  private static Map<Mutability, ObjectMapperFacade> facades = new HashMap<>(2)
  static final AbstractTypeMappingRegistry ABSTRACT_TYPE_MAPPING_REGISTRY = new AbstractTypeMappingRegistry()

  static {
    registerCustomModuleProvider ABSTRACT_TYPE_MAPPING_REGISTRY
  }

  @Synchronized
  static void registerCustomModuleProvider(ModuleProvider moduleProvider) {
    facades.clear()
    CUSTOM_MODULE_PROVIDER_REGISTRY.add moduleProvider
  }

  @Synchronized
  static final ObjectMapperFacade get(Mutability mutability) {
    ObjectMapperFacade facade = facades[mutability]
    Set<Module> customModules = CUSTOM_MODULE_PROVIDER_REGISTRY*.getModule(mutability).toSet()
    if (facade != null && facade.@customModules == customModules) {
      return facade
    }
    ObjectMapper objectMapper = new ObjectMapper()
    objectMapper.serializationInclusion = JsonInclude.Include.NON_NULL
    objectMapper.propertyNamingStrategy = PropertyNamingStrategy.SNAKE_CASE
    objectMapper.registerModule(new ParameterNamesModule(JsonCreator.Mode.PROPERTIES))
    objectMapper.registerModule(new GuavaModule())
    objectMapper.registerModule(new AfterburnerModule())
    facade = new ObjectMapperFacade(objectMapper, customModules)
    facades[mutability] = facade
    facade
  }

  private final ObjectMapper objectMapper
  private final Set<Module> customModules

  private ObjectMapperFacade(ObjectMapper objectMapper, Set<Module> customModules) {
    this.@objectMapper = objectMapper
    this.@customModules = customModules
    customModules.each { Module customModule ->
      objectMapper.registerModule customModule
    }
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
   *<p>
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
   *<p>
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
   * {@link #writeValue(Writer,Object)} with {@link java.io.StringWriter}
   * and constructing String, but more efficient.
   *<p>
   * Note: prior to version 2.1, throws clause included {@link IOException}; 2.1 removed it.
   */
  String writeValueAsString(Object value) throws JsonProcessingException {
    objectMapper.writeValueAsString(value)
  }
}
