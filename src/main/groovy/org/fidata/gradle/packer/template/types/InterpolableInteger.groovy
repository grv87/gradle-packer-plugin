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
  Object value

  @Override
  protected Integer doInterpolatePrimitive(Context ctx) {
    if (Integer.isInstance(value)) {
      (Integer)value
    } else {
      ctx.interpolateString((String)value).toInteger()
    }
  }

  static class InterpolableIntegerSerializer extends JsonSerializer<InterpolableInteger> {
    @Override
    void serialize(InterpolableInteger value, JsonGenerator gen, SerializerProvider serializers) throws IOException, JsonProcessingException {
      if (Integer.isInstance(value.value)) {
        gen.writeNumber((Integer)value.value)
      } else {
        gen.writeString((String)value.value)
      }
    }
  }

  static class InterpolableIntegerDeserializer extends JsonDeserializer<InterpolableInteger> {
    @Override
    InterpolableInteger deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException, JsonProcessingException {
      if (jp.currentToken == JsonToken.VALUE_NUMBER_INT) {
        return new InterpolableInteger(
          value: jp.readValueAs(Integer)
        )
      } else {
        return new InterpolableInteger(
          value: jp.readValueAs(String)
        )
      }
    }
  }
}
