package org.fidata.gradle.packer.template.types

import com.fasterxml.jackson.annotation.JsonCreator
import groovy.transform.CompileStatic
import org.fidata.gradle.packer.template.Context
import org.fidata.gradle.packer.template.internal.InterpolableSinglePrimitive

@CompileStatic
class InterpolableFile extends InterpolableSinglePrimitive<String, File> {
  @JsonCreator
  InterpolableFile(String rawValue) {
    super(rawValue)
  }

  @Override
  protected File doInterpolatePrimitive(Context ctx) {
    ctx.interpolateFile(rawValue)
  }
}
