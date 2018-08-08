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

@JsonSerialize(using = InterpolableBooleanSerializer)
@JsonDeserialize(using = InterpolableBooleanDeserializer)
@CompileStatic
class InterpolableBoolean extends InterpolablePrimitive<Boolean> {
  Object rawValue

  @Override
  protected Boolean doInterpolatePrimitive(Context ctx) {
    if (Boolean.isInstance(rawValue)) {
      (Boolean)rawValue
    } else {
      ctx.interpolateString((String)rawValue).toBoolean() // TOTEST
    }
  }

  static class InterpolableBooleanSerializer extends JsonSerializer<InterpolableBoolean> {
    @Override
    void serialize(InterpolableBoolean value, JsonGenerator gen, SerializerProvider serializers) throws IOException, JsonProcessingException {
      Object rawValue = value.rawValue
      if (Integer.isInstance(rawValue)) {
        gen.writeNumber((Integer)rawValue)
      } else {
        gen.writeString((String)rawValue)
      }
    }
  }

  static class InterpolableBooleanDeserializer extends JsonDeserializer<InterpolableBoolean> {
    @Override
    InterpolableBoolean deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException, JsonProcessingException {
      Object rawValue
      if (jp.currentToken == JsonToken.VALUE_TRUE || jp.currentToken == JsonToken.VALUE_FALSE) {
        rawValue = jp.readValueAs(Boolean)
      } else {
        rawValue = jp.readValueAs(String)
      }
      return new InterpolableBoolean(
        rawValue: rawValue
      )
    }
  }
}
