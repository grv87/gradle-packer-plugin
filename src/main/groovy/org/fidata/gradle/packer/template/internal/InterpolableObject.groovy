package org.fidata.gradle.packer.template.internal

import groovy.transform.CompileStatic
import org.fidata.gradle.packer.template.Context

@CompileStatic
abstract class InterpolableObject implements Serializable, Cloneable {
  private boolean interpolated = false

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
