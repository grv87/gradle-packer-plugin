package org.fidata.gradle.packer.template.internal

import com.fasterxml.jackson.annotation.JsonIgnore
import groovy.transform.CompileStatic
import org.fidata.gradle.packer.template.Context
import org.gradle.api.tasks.Internal

@CompileStatic
abstract class InterpolableObject implements Serializable/*, Cloneable*/ {
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

  public void interpolate(Context context) {
    if (!interpolated) {
      this.context = context
      doInterpolate()
      interpolated = true
    } else if (this.context != context) {
      throw new IllegalStateException('Object is already interpolated with different context')
    }
  }

  abstract protected void doInterpolate()

  /*InterpolableObject clone() {
    InterpolableObject result = (InterpolableObject)super.clone()

    result
  }*/
}
