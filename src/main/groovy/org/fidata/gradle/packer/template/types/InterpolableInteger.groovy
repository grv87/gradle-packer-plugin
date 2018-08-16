package org.fidata.gradle.packer.template.types

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.core.JsonParseException
import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.core.JsonProcessingException
import com.fasterxml.jackson.core.JsonToken
import com.fasterxml.jackson.databind.DeserializationContext
import com.fasterxml.jackson.databind.deser.std.StdDeserializer
import com.fasterxml.jackson.databind.annotation.JsonDeserialize
import groovy.transform.CompileStatic
import org.fidata.gradle.packer.template.Context
import org.fidata.gradle.packer.template.internal.InterpolableSinglePrimitive

// @JsonDeserialize(using = InterpolableIntegerDeserializer)
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
    } else if (InterpolableString.isInstance(rawValue)) {
      ((InterpolableString)rawValue).interpolate ctx
      ((InterpolableString)rawValue).interpolatedValue.toInteger()
    } else {
      throw new IllegalStateException(sprintf('Invalid interpolable integer raw value: %s', [rawValue]))
    }
  }

  @JsonCreator
  InterpolableInteger(Integer rawValue) {
    super(rawValue)
  }

  @JsonCreator
  InterpolableInteger(InterpolableString rawValue) {
    super(rawValue)
  }

  /*static class InterpolableIntegerDeserializer extends StdDeserializer<InterpolableInteger> {
    @Override
    InterpolableInteger deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException, JsonProcessingException {
      Class rawValueClass
      switch (jp.currentToken) {
        case JsonToken.VALUE_NUMBER_INT:
          rawValueClass = Integer
          break
        case JsonToken.VALUE_STRING:
          rawValueClass = InterpolableString
          break
        default:
          throw new JsonParseException(jp, 'invalid integer value')
      }
      return new InterpolableInteger(
        rawValue: jp.readValueAs(rawValueClass)
      )
    }

    InterpolableIntegerDeserializer() {
      super(InterpolableInteger)
    }
  }*/
}
