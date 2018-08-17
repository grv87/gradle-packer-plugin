package org.fidata.gradle.packer.template.utils

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonValue
import groovy.transform.CompileStatic
import org.fidata.gradle.packer.template.Context
import org.fidata.gradle.packer.template.PostProcessor
import org.fidata.gradle.packer.template.internal.InterpolableObject
import org.gradle.api.tasks.Internal

@CompileStatic
class PostProcessorDefinition extends InterpolableObject {
  @JsonValue
  @Internal
  Object rawValue

  @JsonCreator
  PostProcessorDefinition(String rawValue) {
    this.rawValue = rawValue
  }

  @JsonCreator
  PostProcessorDefinition(PostProcessor rawValue) {
    this.rawValue = rawValue
  }

  @Override
  protected void doInterpolate(Context ctx) {
    // TODO
  }
}
