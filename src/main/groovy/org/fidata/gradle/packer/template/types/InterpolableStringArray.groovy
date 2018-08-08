package org.fidata.gradle.packer.template.types

import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.core.JsonProcessingException
import com.fasterxml.jackson.core.JsonToken
import com.fasterxml.jackson.databind.DeserializationContext
import com.fasterxml.jackson.databind.JsonDeserializer
import com.fasterxml.jackson.databind.JsonSerializer
import com.fasterxml.jackson.databind.SerializerProvider
import com.fasterxml.jackson.databind.annotation.JsonDeserialize
import com.fasterxml.jackson.databind.annotation.JsonSerialize
import groovy.transform.CompileStatic
import org.fidata.gradle.packer.template.Context
import org.fidata.gradle.packer.template.internal.InterpolablePrimitive

@JsonSerialize(using = InterpolableStringListSerializer)
@JsonDeserialize(using = InterpolableStringListDeserializer)
@CompileStatic
class InterpolableStringArray extends InterpolablePrimitive<List<String>> {
  static class ArrayClass extends ArrayList<InterpolableString> {}

  List<InterpolableString> array
  InterpolableString variable

  static class InterpolableStringListSerializer extends JsonSerializer<InterpolableStringArray> {
    @Override
    void serialize(InterpolableStringArray value, JsonGenerator gen, SerializerProvider serializers) throws IOException, JsonProcessingException {
      if (value.variable) {
        serializers.findValueSerializer(InterpolableString).serialize value.variable, gen, serializers
      } else {
        serializers.findValueSerializer(ArrayClass).serialize value.array, gen, serializers
      }
    }
  }

  static class InterpolableStringListDeserializer extends JsonDeserializer<InterpolableStringArray> {
    @Override
    InterpolableStringArray deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException, JsonProcessingException {
      if (jp.currentToken == JsonToken.START_ARRAY) {
        return new InterpolableStringArray(
          array: jp.readValueAs(ArrayClass)
        )
      } else {
        return new InterpolableStringArray(
          variable: jp.readValueAs(InterpolableString)
        )
      }
    }
  }

  @Override
  protected List<String> doInterpolatePrimitive(Context ctx) {
    // TODO
    null
  }
}
