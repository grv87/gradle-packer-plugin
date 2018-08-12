package org.fidata.gradle.packer.template.types

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.core.JsonParseException
import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.core.JsonProcessingException
import com.fasterxml.jackson.core.JsonToken
import com.fasterxml.jackson.databind.DeserializationContext
import com.fasterxml.jackson.databind.JsonDeserializer
import com.fasterxml.jackson.databind.annotation.JsonDeserialize
import groovy.transform.CompileStatic
import org.fidata.gradle.packer.template.Context
import org.fidata.gradle.packer.template.internal.InterpolableSinglePrimitive

@JsonDeserialize(using = InterpolableBooleanDeserializer)
@CompileStatic
class InterpolableBoolean extends InterpolableSinglePrimitive<Object, Boolean> {
  @JsonCreator
  InterpolableBoolean(Object rawValue) {
    super(rawValue)
  }

  @Override
  protected Boolean doInterpolatePrimitive(Context ctx) {
    if (Boolean.isInstance(rawValue)) {
      (Boolean)rawValue
    } else if (InterpolableString.isInstance(rawValue)) {
      ((InterpolableString)rawValue).interpolate ctx
      ((InterpolableString)rawValue).interpolatedValue.toBoolean() // TOTEST
    } else {
      throw new IllegalStateException(sprintf('Invalid interpolable boolean raw value: %s', [rawValue]))
    }
  }

  static class InterpolableBooleanDeserializer extends JsonDeserializer<InterpolableBoolean> {
    @Override
    InterpolableBoolean deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException, JsonProcessingException {
      Class rawValueClass
      switch (jp.currentToken) {
        case JsonToken.VALUE_TRUE:
        case JsonToken.VALUE_FALSE:
          rawValueClass = Boolean
          break
        case JsonToken.VALUE_STRING:
          rawValueClass = InterpolableString
          break
        default:
          throw new JsonParseException(jp, 'invalid boolean value')
      }
      return new InterpolableBoolean(
        rawValue: jp.readValueAs(rawValueClass)
      )
    }
  }
}
