package org.fidata.gradle.packer.template.utils

import com.fasterxml.jackson.annotation.JsonValue
import com.fasterxml.jackson.core.JsonParseException
import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.core.JsonProcessingException
import com.fasterxml.jackson.core.JsonToken
import com.fasterxml.jackson.databind.DeserializationContext
import com.fasterxml.jackson.databind.JsonDeserializer
import com.fasterxml.jackson.databind.annotation.JsonDeserialize
import groovy.transform.CompileStatic
import org.fidata.gradle.packer.template.Context
import org.fidata.gradle.packer.template.PostProcessor
import org.fidata.gradle.packer.template.internal.InterpolableObject
import org.gradle.api.tasks.Internal

@JsonDeserialize(using = PostProcessorsDefinitionDeserializer)
@CompileStatic
class PostProcessorDefinition extends InterpolableObject {
  @JsonValue
  @Internal
  Object rawValue

  @Override
  protected void doInterpolate(Context ctx) {
    // TODO
  }

  static class PostProcessorsDefinitionDeserializer extends JsonDeserializer<PostProcessorDefinition> {
    @Override
    PostProcessorDefinition deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException, JsonProcessingException {
      Class rawValueClass
      switch (jp.currentToken()) {
        case JsonToken.VALUE_STRING:
          rawValueClass = String
          break
        case JsonToken.START_OBJECT:
          rawValueClass = PostProcessor
          break
        default:
          throw new JsonParseException(jp, 'post-processor: bad format')
      }
      return new PostProcessorDefinition(
        rawValue: jp.readValueAs(rawValueClass)
      )
    }
  }
}
