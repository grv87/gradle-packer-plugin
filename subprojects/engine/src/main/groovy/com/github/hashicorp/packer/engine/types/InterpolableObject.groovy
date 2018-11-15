package com.github.hashicorp.packer.engine.types

import com.github.hashicorp.packer.engine.annotations.ComputedInternal
import groovy.transform.AutoClone
import groovy.transform.AutoCloneStyle
import groovy.transform.CompileStatic
import com.github.hashicorp.packer.template.Context

@AutoClone(style = AutoCloneStyle.SIMPLE)
@CompileStatic
abstract class InterpolableObject {
  private final $INTERPOLATION_LOCK = new Object()

  private volatile boolean interpolated = false
  private Context context

  @ComputedInternal
  final Context getContext() {
    this.context
  }

  @ComputedInternal
  final boolean isInterpolated() {
    this.interpolated
  }

  final void interpolate(Context context) throws IllegalStateException {
    if (!interpolated) {
      synchronized($INTERPOLATION_LOCK) {
        if (!interpolated) {
          this.context = context
          doInterpolate()
          interpolated = true
          return
        }
      }
    }
    throw new IllegalStateException('Object is already interpolated')
  }

  abstract protected void doInterpolate()
}
