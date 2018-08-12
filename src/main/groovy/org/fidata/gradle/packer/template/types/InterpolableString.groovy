package org.fidata.gradle.packer.template.types

import com.fasterxml.jackson.annotation.JsonCreator
import groovy.transform.CompileStatic
import org.fidata.gradle.packer.template.Context
import org.fidata.gradle.packer.template.internal.InterpolableSinglePrimitive

@CompileStatic
class InterpolableString extends InterpolableSinglePrimitive<String, String> {
  @JsonCreator
  InterpolableString(String rawValue) {
    super(rawValue)
  }

  @Override
  protected String doInterpolatePrimitive(Context ctx) {
    ctx.interpolateString(rawValue)
  }
}
