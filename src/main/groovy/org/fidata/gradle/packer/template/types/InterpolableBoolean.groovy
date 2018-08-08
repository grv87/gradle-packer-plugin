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
  Object value

  @Override
  protected Boolean doInterpolatePrimitive(Context ctx) {
    if (Boolean.isInstance(value)) {
      (Boolean)value
    } else {
      ctx.interpolateString((String)value).toBoolean() // TOTEST
    }
  }

  static class InterpolableBooleanSerializer extends JsonSerializer<InterpolableBoolean> {
    @Override
    void serialize(InterpolableBoolean value, JsonGenerator gen, SerializerProvider serializers) throws IOException, JsonProcessingException {
      if (Integer.isInstance(value.value)) {
        gen.writeNumber((Integer)value.value)
      } else {
        gen.writeString((String)value.value)
      }
    }
  }

  static class InterpolableBooleanDeserializer extends JsonDeserializer<InterpolableBoolean> {
    @Override
    InterpolableBoolean deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException, JsonProcessingException {
      if (jp.currentToken == JsonToken.VALUE_TRUE || jp.currentToken == JsonToken.VALUE_FALSE) {
        return new InterpolableBoolean(
          value: jp.readValueAs(Boolean)
        )
      } else {
        return new InterpolableBoolean(
          value: jp.readValueAs(String)
        )
      }
    }
  }
}
