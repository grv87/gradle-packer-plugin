package com.github.hashicorp.packer.engine.types

import com.github.hashicorp.packer.engine.enums.InterpolationStage
import groovy.transform.AutoClone
import groovy.transform.AutoCloneStyle
import groovy.transform.CompileStatic
import com.fasterxml.jackson.annotation.JsonIgnore
import org.gradle.api.tasks.Internal
import com.github.hashicorp.packer.template.Context

@AutoClone(style = AutoCloneStyle.SIMPLE/*, excludes = ['interpolated', 'context']*/)
@CompileStatic
abstract class InterpolableObject {
  private InterpolationStage interpolated = null
  private Context context

  @JsonIgnore
  @Internal
  Context getContext() {
    this.context
  }

  @JsonIgnore
  @Internal
  boolean isInterpolated() {
    this.interpolated
  }

  final void interpolate(Context context, InterpolationStage stage) throws IllegalStateException {
    if (interpolated == null || interpolated < stage) {
      this.context = context
      doInterpolate()
      interpolated = true
    } else if (this.context != context) {
      throw new IllegalStateException('Object is already interpolated with different context')
    }
  }

  abstract protected void doInterpolate(InterpolationStage stage)





  /*protected static final <Target extends Serializable, Value extends InterpolableValue<?, Target>> void interpolateValueWithDefault(Value value, Context context, Target aDefault) {
    if (value) {
      value.interpolate context
    } else {
      value =

    }
  }*/
}
