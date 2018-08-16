package org.fidata.gradle.packer.template.internal

import com.fasterxml.jackson.annotation.JsonIgnore
import groovy.transform.CompileStatic
import org.fidata.gradle.packer.template.Context
import org.gradle.api.tasks.Internal

@CompileStatic
abstract class InterpolableObject implements Serializable, Cloneable {
  private boolean interpolated = false

  @JsonIgnore
  @Internal
  boolean isInterpolated() {
    this.interpolated
  }

  public void interpolate(Context ctx) {
    if (!interpolated) {
      doInterpolate ctx
      interpolated = true
    }
  }

  abstract protected void doInterpolate(Context ctx)
}
