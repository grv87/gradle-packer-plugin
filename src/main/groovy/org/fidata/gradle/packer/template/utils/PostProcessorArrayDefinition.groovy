package org.fidata.gradle.packer.template.utils

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonValue
import groovy.transform.CompileStatic
import org.fidata.gradle.packer.template.Context
import org.fidata.gradle.packer.template.internal.InterpolableObject
import org.gradle.api.tasks.Internal

@CompileStatic
class PostProcessorArrayDefinition extends InterpolableObject {
  static class ArrayClass extends ArrayList<PostProcessorDefinition> {}
  @JsonValue
  @Internal
  Object rawValue

  @JsonCreator
  PostProcessorArrayDefinition(ArrayClass rawValue) {
    this.rawValue
  }

  @JsonCreator
  PostProcessorArrayDefinition(PostProcessorDefinition rawValue) {
    this.rawValue
  }

  @Override
  protected void doInterpolate(Context ctx) {
    // TODO
  }
}
