package org.fidata.gradle.packer.template.types

import com.fasterxml.jackson.annotation.JsonCreator
import com.rits.cloning.Immutable
import groovy.transform.CompileStatic
import org.fidata.gradle.packer.template.Context
import org.fidata.gradle.packer.template.enums.Direction
import org.fidata.gradle.packer.template.internal.InterpolableValue

@CompileStatic
@Immutable
class InterpolableDirection extends InterpolableValue<InterpolableString, Direction> {
  @JsonCreator
  InterpolableDirection(InterpolableString rawValue) {
    super(rawValue)
  }

  @Override
  protected Direction doInterpolatePrimitive() {
    rawValue.interpolate context
    Direction.forValue(rawValue.interpolatedValue)
  }
}
