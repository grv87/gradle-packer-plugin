package org.fidata.gradle.packer.template.types

import com.fasterxml.jackson.annotation.JsonCreator
import com.rits.cloning.Immutable
import groovy.transform.CompileStatic
import org.fidata.gradle.packer.template.Context
import org.fidata.gradle.packer.template.internal.InterpolableValue

@CompileStatic
@Immutable
class InterpolableString extends InterpolableValue<String, String> {
  @JsonCreator
  InterpolableString(String rawValue) {
    super(rawValue)
  }

  @Override
  protected String doInterpolatePrimitive() {
    ctx.interpolateString(rawValue)
  }
}
