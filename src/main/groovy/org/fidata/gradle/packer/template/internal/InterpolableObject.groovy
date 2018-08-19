package org.fidata.gradle.packer.template.internal

import com.fasterxml.jackson.annotation.JsonIgnore
import groovy.transform.CompileStatic
import org.fidata.gradle.packer.template.Context
import org.gradle.api.tasks.Internal

@CompileStatic
abstract class InterpolableObject implements Serializable, Cloneable {
  private boolean interpolated = false
  private Context ctx

  protected Context getCtx() {
    new Context(this.ctx)
  }

  @JsonIgnore
  @Internal
  boolean isInterpolated() {
    this.interpolated
  }

  public void interpolate(Context ctx) {
    if (!interpolated) {
      this.ctx = ctx
      doInterpolate()
      interpolated = true
    } else if (!this.ctx.is(ctx)) {
      throw new IllegalStateException('Object is already interpolated with different context')
    }
  }

  abstract protected void doInterpolate()

  InterpolableObject clone() {
    InterpolableObject result = (InterpolableObject)super.clone()

    result
  }
}
