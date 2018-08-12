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

@JsonDeserialize(using = InterpolableStringListDeserializer)
@CompileStatic
class InterpolableStringArray extends InterpolableSinglePrimitive<Object, List<String>> {
  static class ArrayClass extends ArrayList<InterpolableString> {}

  @JsonCreator
  InterpolableStringArray(Object rawValue) {
    super(rawValue)
  }

  @Override
  protected List<String> doInterpolatePrimitive(Context ctx) {
    List<String> result
    if (ArrayClass.isInstance(rawValue)) {
      new ArrayList<String>(((ArrayClass)rawValue).collect { it.interpolate ctx; it.interpolatedValue })
    } else if (InterpolableString.isInstance(rawValue)) {
      ((InterpolableString)rawValue).interpolate ctx
      ((InterpolableString)rawValue).interpolatedValue.split(',').toList()
    } else {
      throw new IllegalStateException(sprintf('Invalid interpolable string array raw value: %s', [rawValue]))
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
