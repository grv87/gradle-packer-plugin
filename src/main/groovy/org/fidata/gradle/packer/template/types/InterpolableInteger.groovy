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

@JsonSerialize(using = InterpolableIntegerSerializer)
@JsonDeserialize(using = InterpolableIntegerDeserializer)
@CompileStatic
class InterpolableInteger extends InterpolablePrimitive<Integer> {
  Object rawValue

  @Override
  protected Integer doInterpolatePrimitive(Context ctx) {
    if (Integer.isInstance(rawValue)) {
      (Integer)rawValue
    } else {
      ctx.interpolateString((String)rawValue).toInteger()
    }
  }

  static class InterpolableIntegerSerializer extends JsonSerializer<InterpolableInteger> {
    @Override
    void serialize(InterpolableInteger value, JsonGenerator gen, SerializerProvider serializers) throws IOException, JsonProcessingException {
      Object rawValue = value.rawValue
      if (Integer.isInstance(rawValue)) {
        gen.writeNumber((Integer)rawValue)
      } else {
        gen.writeString((String)rawValue)
      }
    }
  }

  static class InterpolableIntegerDeserializer extends JsonDeserializer<InterpolableInteger> {
    @Override
    InterpolableInteger deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException, JsonProcessingException {
      Object rawValue
      if (jp.currentToken == JsonToken.VALUE_NUMBER_INT) {
        rawValue = jp.readValueAs(Integer)
      } else {
        rawValue = jp.readValueAs(String)
      }
      return new InterpolableInteger(
        rawValue: rawValue
      )
    }
  }
}
