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
  Object rawValue

  @Override
  protected List<String> doInterpolatePrimitive(Context ctx) {
    List<String> result
    if (ArrayClass.isInstance(rawValue)) {
      new ArrayList<String>(((ArrayClass)rawValue).collect { it.interpolate(ctx); it.interpolatedValue })
    } else {
      ((InterpolableString)rawValue).interpolate(ctx)
      ((InterpolableString)rawValue).interpolatedValue.split(',').toList()
    }
  }

  static class InterpolableStringListSerializer extends JsonSerializer<InterpolableStringArray> {
    @Override
    void serialize(InterpolableStringArray value, JsonGenerator gen, SerializerProvider serializers) throws IOException, JsonProcessingException {
      Object rawValue = value.rawValue
      if (ArrayClass.isInstance(rawValue)) {
        serializers.findValueSerializer(ArrayClass).serialize rawValue, gen, serializers
      } else {
        serializers.findValueSerializer(InterpolableString).serialize rawValue, gen, serializers
      }
    }
  }

  static class InterpolableStringListDeserializer extends JsonDeserializer<InterpolableStringArray> {
    @Override
    InterpolableStringArray deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException, JsonProcessingException {
      Object rawValue
      if (jp.currentToken == JsonToken.START_ARRAY) {
        rawValue = jp.readValueAs(ArrayClass)
      } else {
        rawValue = jp.readValueAs(InterpolableString)
      }
      return new InterpolableStringArray(
        rawValue: rawValue
      )
    }
  }
}
