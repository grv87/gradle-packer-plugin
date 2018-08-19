package org.fidata.gradle.packer.template.types

import com.fasterxml.jackson.annotation.JsonCreator
import com.rits.cloning.Immutable
import groovy.transform.CompileStatic
import org.fidata.gradle.packer.template.Context
import org.fidata.gradle.packer.template.internal.InterpolableValue

@CompileStatic
@Immutable
class InterpolableFile extends InterpolableValue<InterpolableString, File> {
  @JsonCreator
  InterpolableFile(InterpolableString rawValue) {
    super(rawValue)
  }

  @Override
  protected File doInterpolatePrimitive() {
    rawValue.interpolate ctx
    ctx.interpolateFile(rawValue.interpolatedValue) // TODO
  }
}
