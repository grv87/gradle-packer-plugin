package com.github.hashicorp.packer.engine.utils

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.core.JsonParseException
import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.databind.JsonMappingException
import com.fasterxml.jackson.databind.Module
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.module.SimpleModule
import com.github.hashicorp.packer.engine.types.InterpolableBoolean
import com.github.hashicorp.packer.engine.types.InterpolableLong
import com.github.hashicorp.packer.engine.types.InterpolableObject
import com.github.hashicorp.packer.engine.types.InterpolableString
import com.fasterxml.jackson.module.paramnames.ParameterNamesModule
import com.fasterxml.jackson.datatype.guava.GuavaModule
import com.fasterxml.jackson.databind.PropertyNamingStrategy
import com.fasterxml.jackson.module.afterburner.AfterburnerModule
import groovy.transform.Synchronized

final class ObjectMapperFacade {
  private static final Set<ModuleProvider> CUSTOM_MODULE_PROVIDER_REGISTRY = new HashSet()
  private static final Map<Class<? extends InterpolableObject>, Map<Mutability, Class<? extends InterpolableObject>>> ABSTRACT_TYPE_MAPPING_REGISTRY = [:]
  private static Map<Mutability, ObjectMapperFacade> facades = new HashMap<>(2)
  static final AbstractTypeMappingRegistry ABSTRACT_TYPE_MODULE_REGISTRY = new AbstractTypeMappingRegistry()

  static {
    registerCustomModuleProvider ABSTRACT_TYPE_MODULE_REGISTRY
  }

  @Synchronized
  static void registerCustomModuleProvider(ModuleProvider moduleProvider) {
    facades.clear()
    CUSTOM_MODULE_PROVIDER_REGISTRY.add moduleProvider
  }

  private static final Set<Module> getCustomModules(Mutability mutability) {
    CUSTOM_MODULE_PROVIDER_REGISTRY*.getModule(mutability).toSet()
  }

  @Synchronized
  static final ObjectMapperFacade get(Mutability mutability) {
    ObjectMapperFacade facade = facades[mutability]
    Set<Module> customModules = getCustomModules(mutability)
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
}
