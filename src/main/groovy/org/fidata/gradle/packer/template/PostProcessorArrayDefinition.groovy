package org.fidata.gradle.packer.template

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonValue
import groovy.transform.AutoClone
import groovy.transform.AutoCloneStyle
import groovy.transform.CompileStatic
import org.fidata.gradle.packer.template.internal.InterpolableObject
import org.gradle.api.tasks.Internal

@AutoClone(style = AutoCloneStyle.SIMPLE)
@CompileStatic
class PostProcessorArrayDefinition extends InterpolableObject {
  static class ArrayClass extends ArrayList<PostProcessorDefinition> {
  }

  @JsonValue
  @Internal
  Object rawValue

  private PostProcessorArrayDefinition() {
  }

  @JsonCreator
  PostProcessorArrayDefinition(ArrayClass rawValue) {
    this.rawValue = rawValue
  }

  @JsonCreator
  PostProcessorArrayDefinition(PostProcessorDefinition rawValue) {
    this.rawValue = rawValue
  }

  @Override
  protected void doInterpolate() {
    if (ArrayClass.isInstance(rawValue)) {
      ((ArrayClass)rawValue).each { PostProcessorDefinition postProcessorDefinition -> postProcessorDefinition.interpolate context }
    } else if (PostProcessorDefinition.isInstance(rawValue)) {
      ((PostProcessorDefinition)rawValue).interpolate context
    }
  }

  PostProcessorArrayDefinition interpolateForBuilder(Context buildCtx) {
    if (ArrayClass.isInstance(rawValue)) {
      ArrayClass result = (ArrayClass)((ArrayClass)rawValue)*.interpolateForBuilder(buildCtx)
      if (result.size() > 0) {
        new PostProcessorArrayDefinition(result)
      } else {
        null
      }
    } else if (PostProcessorDefinition.isInstance(rawValue)) {
      PostProcessorDefinition result = ((PostProcessorDefinition)rawValue).interpolateForBuilder(buildCtx)
      if (result) {
        new PostProcessorArrayDefinition(result)
      } else {
        null
      }
    } else {
      throw new IllegalStateException(sprintf('Invalid rawValue class: %s', [rawValue.class]))
    }
  }
}
