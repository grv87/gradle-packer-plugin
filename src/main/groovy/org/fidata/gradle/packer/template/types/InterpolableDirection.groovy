package org.fidata.gradle.packer.template.types

import com.fasterxml.jackson.annotation.JsonCreator
import groovy.transform.CompileStatic
import org.fidata.gradle.packer.template.Context
import org.fidata.gradle.packer.template.enums.Direction
import org.fidata.gradle.packer.template.internal.InterpolablePrimitive

@CompileStatic
class InterpolableDirection extends InterpolablePrimitive<String, Direction> {
  @JsonCreator
  InterpolableDirection(String rawValue) {
    super(rawValue)
  }

  @Override
  protected Direction doInterpolatePrimitive(Context ctx) {
    Direction.forValue(ctx.interpolateString(rawValue))
  }
}
