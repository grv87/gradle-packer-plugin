package org.fidata.gradle.packer.template.types

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonValue
import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.core.JsonProcessingException
import com.fasterxml.jackson.core.JsonToken
import com.fasterxml.jackson.databind.DeserializationContext
import com.fasterxml.jackson.databind.JsonDeserializer
import com.fasterxml.jackson.databind.JsonSerializable
import com.fasterxml.jackson.databind.JsonSerializer
import com.fasterxml.jackson.databind.SerializerProvider
import com.fasterxml.jackson.databind.annotation.JsonDeserialize
import com.fasterxml.jackson.databind.annotation.JsonSerialize
import groovy.transform.CompileStatic
import org.fidata.gradle.packer.template.Context
import org.fidata.gradle.packer.template.internal.TemplateObject
import org.gradle.api.tasks.Internal

// @JsonSerialize(using = TemplateStringSerializer)
// @JsonDeserialize(using = TemplateStringDeserializer)
@CompileStatic
class TemplateString extends TemplateObject /*implements JsonSerializable*/ {
  @JsonValue
  String value

  @JsonCreator
  TemplateString(String value) {
    this.value = value
  }

  private String interpolatedValue

  String getInterpolatedValue() {
    interpolatedValue
  }

  @Override
  protected void doInterpolate(Context ctx) {
    interpolatedValue = ctx.interpolateString(value)
  }

  @Override
  boolean equals(Object obj) {
    this.class.isInstance(obj) && ((TemplateString)obj).interpolatedValue == interpolatedValue
  }

  private static final long serialVersionUID = 1L

  /*private void writeObject(ObjectOutputStream out) throws IOException {
    out.writeChars(interpolatedValue)
  }*/
  private Object writeReplace() throws ObjectStreamException {
    interpolatedValue
  }

  /* TOTEST TemplateString() {
  }*/
}
class TemplateStringSerializer extends JsonSerializer<TemplateString> {
  @Override
  void serialize(TemplateString value, JsonGenerator gen, SerializerProvider serializers) throws IOException, JsonProcessingException {
    // serializers.findValueSerializer(String).serialize value.value, gen, serializers
    gen.writeString(value.value)
  }
}

class TemplateStringDeserializer extends JsonDeserializer<TemplateString> {
  @Override
  TemplateString deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException, JsonProcessingException {
    new TemplateString(value: jp.valueAsString)
  }
}
