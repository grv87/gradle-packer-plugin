package com.github.hashicorp.packer.engine.utils

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.core.JsonParseException
import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.core.type.ResolvedType
import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.JavaType
import com.fasterxml.jackson.databind.JsonMappingException
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.PropertyNamingStrategy
import com.fasterxml.jackson.databind.jsontype.NamedType
import com.github.hashicorp.packer.engine.types.InterpolableBoolean
import com.github.hashicorp.packer.engine.types.InterpolableLong
import com.github.hashicorp.packer.engine.types.InterpolableString
import com.github.hashicorp.packer.engine.types.InterpolableValue
import com.fasterxml.jackson.databind.module.SimpleModule
import com.fasterxml.jackson.module.paramnames.ParameterNamesModule
import com.fasterxml.jackson.datatype.guava.GuavaModule
import com.fasterxml.jackson.databind.PropertyNamingStrategy
import com.fasterxml.jackson.module.afterburner.AfterburnerModule

final class ObjectMapperProvider {
  private static final ObjectMapper MAPPER = new ObjectMapper()
  static {
    MAPPER.registerModule(new ParameterNamesModule(JsonCreator.Mode.PROPERTIES))
    MAPPER.registerModule(new GuavaModule())
    MAPPER.registerModule(new AfterburnerModule())
    MAPPER.propertyNamingStrategy = PropertyNamingStrategy.SNAKE_CASE
    MAPPER.serializationInclusion = JsonInclude.Include.NON_NULL
    MAPPER.registerModule(InterpolableValue.SERIALIZER_MODULE)
    /*
     * TODO:
     * 1. All classes
     * 2. Mutable and immutable versions
     */
    SimpleModule immutableModule = new SimpleModule()
    immutableModule.addAbstractTypeMapping(InterpolableBoolean, InterpolableBoolean.ImmutableRaw)
    immutableModule.addAbstractTypeMapping(InterpolableString, InterpolableString.ImmutableRaw)
    immutableModule.addAbstractTypeMapping(InterpolableLong, InterpolableLong.ImmutableRaw)
    MAPPER.registerModule(immutableModule);
  }
  
  static void registerSubtype(String type, Class<?> clazz) {
    MAPPER.registerSubtypes(new NamedType(clazz, type))
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
  static <T> T readValue(File src, Class<T> valueType) throws IOException, JsonParseException, JsonMappingException {
    MAPPER.readValue(src, valueType)
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
  static <T> T readValue(URL src, Class<T> valueType) throws IOException, JsonParseException, JsonMappingException {
    MAPPER.readValue(src, valueType)
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
  static <T> T readValue(String content, Class<T> valueType) throws IOException, JsonParseException, JsonMappingException {
    MAPPER.readValue(content, valueType)
  }

  @SuppressWarnings('unchecked')
  static <T> T readValue(Reader src, Class<T> valueType) throws IOException, JsonParseException, JsonMappingException {
    MAPPER.readValue(src, valueType)
  }

  @SuppressWarnings('unchecked')
  static <T> T readValue(InputStream src, Class<T> valueType) throws IOException, JsonParseException, JsonMappingException {
    MAPPER.readValue(src, valueType)
  }

  // Suppress default constructor for noninstantiability
  private ObjectMapperProvider() {
    throw new UnsupportedOperationException()
  }
}
