package org.fidata.gradle.packer.template.internal

import groovy.transform.AutoClone
import groovy.transform.AutoCloneStyle
import groovy.transform.CompileStatic
import com.fasterxml.jackson.annotation.JsonIgnore
import org.gradle.api.tasks.Internal
import org.fidata.gradle.packer.template.Context

@AutoClone(style = AutoCloneStyle.SIMPLE, excludes = ['interpolated', 'context'])
@CompileStatic
abstract class InterpolableObject {
  private boolean interpolated = false
  private Context context

  @JsonIgnore
  @Internal
  Context getContext() {
    context
  }

  @JsonIgnore
  @Internal
  boolean isInterpolated() {
    this.interpolated
  }

  void interpolate(Context context) throws IllegalStateException {
    if (!interpolated) {
      this.context = context
      doInterpolate()
      interpolated = true
    } else if (this.context != context) {
      throw new IllegalStateException('Object is already interpolated with different context')
    }
  }

  abstract protected void doInterpolate()
}
