package org.fidata.gradle.packer.template.types

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.core.JsonProcessingException
import com.fasterxml.jackson.core.JsonToken
import com.fasterxml.jackson.databind.DeserializationContext
import com.fasterxml.jackson.databind.JsonDeserializer
import com.fasterxml.jackson.databind.annotation.JsonDeserialize
import groovy.transform.CompileStatic
import org.fidata.gradle.packer.template.Context
import org.fidata.gradle.packer.template.internal.InterpolablePrimitive

@JsonDeserialize(using = InterpolableBooleanDeserializer)
@CompileStatic
class InterpolableBoolean extends InterpolablePrimitive<Object, Boolean> {
  @JsonCreator
  InterpolableBoolean(Object rawValue) {
    super(rawValue)
  }

  @Override
  protected Boolean doInterpolatePrimitive(Context ctx) {
    if (Boolean.isInstance(rawValue)) {
      (Boolean)rawValue
    } else {
      ctx.interpolateString((String)rawValue).toBoolean() // TOTEST
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
