package org.fidata.gradle.packer.template.types

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonValue
import groovy.transform.CompileStatic
import org.fidata.gradle.packer.template.Context
import org.fidata.gradle.packer.template.internal.InterpolablePrimitive

@CompileStatic
class InterpolableString extends InterpolablePrimitive<String> {
  @JsonValue
  String value

  @JsonCreator
  InterpolableString(String value) {
    this.value = value
  }

  @Override
  protected String doInterpolatePrimitive(Context ctx) {
    ctx.interpolateString(value)
  }
}
