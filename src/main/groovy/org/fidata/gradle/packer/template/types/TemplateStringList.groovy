package org.fidata.gradle.packer.template.types

import com.fasterxml.jackson.annotation.JsonIgnore
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
import com.fasterxml.jackson.databind.ser.std.JsonValueSerializer
import org.fidata.gradle.packer.template.Context
import org.fidata.gradle.packer.template.internal.TemplateObject

@JsonSerialize(using = TemplateStringList.TemplateStringListSerializer)
@JsonDeserialize(using = TemplateStringList.TemplateStringListDeserializer)
class TemplateStringList extends TemplateObject {
  static class ArrayClass extends ArrayList<TemplateString> {}

  List<TemplateString> array
  TemplateString variable

  static class TemplateStringListSerializer extends JsonSerializer<TemplateStringList> {
    @Override
    void serialize(TemplateStringList value, JsonGenerator gen, SerializerProvider serializers) throws IOException, JsonProcessingException {
      if (value.variable) {
        serializers.findValueSerializer(TemplateString).serialize value.variable, gen, serializers
      } else {
        serializers.findValueSerializer(ArrayClass).serialize value.array, gen, serializers
      }
    }
  }

  static class TemplateStringListDeserializer extends JsonDeserializer<TemplateStringList> {
    @Override
    TemplateStringList deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException, JsonProcessingException {
      if (jp.currentToken == JsonToken.START_ARRAY) {
        return new TemplateStringList(
          array: jp.readValueAs(ArrayClass)
        )
      } else {
        return new TemplateStringList(
          variable: jp.readValueAs(TemplateString)
        )
      }
    }
  }

  @Override
  protected void doInterpolate(Context ctx) {

  }
}
