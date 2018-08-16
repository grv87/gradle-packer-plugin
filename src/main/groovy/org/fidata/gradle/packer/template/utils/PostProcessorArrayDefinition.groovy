package org.fidata.gradle.packer.template.utils

import com.fasterxml.jackson.annotation.JsonValue
import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.core.JsonProcessingException
import com.fasterxml.jackson.core.JsonToken
import com.fasterxml.jackson.databind.DeserializationContext
import com.fasterxml.jackson.databind.JsonDeserializer
import com.fasterxml.jackson.databind.annotation.JsonDeserialize
import groovy.transform.CompileStatic
import org.fidata.gradle.packer.template.Context
import org.fidata.gradle.packer.template.internal.InterpolableObject
import org.gradle.api.tasks.Internal

@JsonDeserialize(using = PostProcessorArrayDefinitionDeserializer)
@CompileStatic
class PostProcessorArrayDefinition extends InterpolableObject {
  static class ArrayClass extends ArrayList<PostProcessorDefinition> {}
  @JsonValue
  @Internal
  Object rawValue

  @Override
  protected void doInterpolate(Context ctx) {
    // TODO
  }

  static class PostProcessorArrayDefinitionDeserializer extends JsonDeserializer<PostProcessorArrayDefinition> {
    @Override
    PostProcessorArrayDefinition deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException, JsonProcessingException {
      return new PostProcessorArrayDefinition(
        rawValue: jp.readValueAs(jp.currentToken() == JsonToken.START_ARRAY ? ArrayClass : PostProcessorDefinition)
      )
    }
  }
}
