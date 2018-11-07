package com.github.hashicorp.packer.engine.types

import groovy.transform.AutoClone
import groovy.transform.AutoCloneStyle
import groovy.transform.CompileStatic
import com.fasterxml.jackson.annotation.JsonIgnore
import groovy.transform.PackageScope
import org.gradle.api.tasks.Internal
import com.github.hashicorp.packer.template.Context

@AutoClone(style = AutoCloneStyle.SIMPLE)
@CompileStatic
abstract class InterpolableObject {
  // It can be changed in InterpolableValue.forInterpolatedValue
  /*private*/ @PackageScope boolean interpolated = false
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

  final void interpolate(Context context) throws IllegalStateException {
    if (!interpolated) {
      this.context = context
      doInterpolate()
      interpolated = true
    } else if (this.context != context) {
      throw new IllegalStateException('Object is already interpolated with different context')
    }
  }

  abstract protected void doInterpolate()

  /*protected static final <Target extends Serializable, Value extends InterpolableValue<?, Target>> void interpolateValueWithDefault(Value value, Context context, Target aDefault) {
    if (value) {
      value.interpolate context
    } else {
      value =

    }
  }*/
}
