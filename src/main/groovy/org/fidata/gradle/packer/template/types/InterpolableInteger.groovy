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
import org.fidata.gradle.packer.template.internal.InterpolableSinglePrimitive

@JsonDeserialize(using = InterpolableIntegerDeserializer)
@CompileStatic
class InterpolableInteger extends InterpolableSinglePrimitive<Object, Integer> {
  @JsonCreator
  InterpolableInteger(Object rawValue) {
    super(rawValue)
  }

  @Override
  protected Integer doInterpolatePrimitive(Context ctx) {
    if (Integer.isInstance(rawValue)) {
      (Integer)rawValue
    } else {
      ctx.interpolateString((String)rawValue).toInteger()
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
