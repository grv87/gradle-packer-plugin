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
class PostProcessorDefinition extends InterpolableObject {
  @JsonValue
  @Internal
  Object rawValue

  private PostProcessorDefinition() {
  }

  @JsonCreator
  PostProcessorDefinition(String rawValue) {
    this.rawValue = rawValue
  }

  @JsonCreator
  PostProcessorDefinition(PostProcessor rawValue) {
    this.rawValue = rawValue
  }

  @Override
  protected void doInterpolate() {
    if (PostProcessor.isInstance(rawValue)) {
      ((PostProcessor)rawValue).interpolate context
    }
  }

  PostProcessorDefinition interpolateForBuilder(Context buildCtx) {
    if (PostProcessor.isInstance(rawValue)) {
      PostProcessor result = ((PostProcessor)rawValue).interpolateForBuilder(buildCtx)
      if (result) {
        new PostProcessorDefinition(result)
      } else {
        null
      }
    } else if (String.isInstance(rawValue)) {
      new PostProcessorDefinition((String)rawValue)
    } else {
      throw new IllegalStateException(sprintf('Invalid rawValue class: %s', [rawValue.class]))
    }
  }
}
