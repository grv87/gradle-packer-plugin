package org.fidata.gradle.packer.template.types

import com.fasterxml.jackson.annotation.JsonCreator
import groovy.transform.CompileStatic
import org.fidata.gradle.packer.template.Context
import org.fidata.gradle.packer.template.internal.InterpolableSinglePrimitive

@CompileStatic
class InterpolableFile extends InterpolableSinglePrimitive<InterpolableString, File> {
  @JsonCreator
  InterpolableFile(InterpolableString rawValue) {
    super(rawValue)
  }

  @Override
  protected File doInterpolatePrimitive(Context ctx) {
    rawValue.interpolate ctx
    ctx.interpolateFile(rawValue.interpolatedValue) // TODO
  }
}
