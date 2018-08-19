package org.fidata.gradle.packer.template.types

import groovy.transform.AutoClone
import groovy.transform.AutoCloneStyle
import groovy.transform.InheritConstructors
import groovy.transform.CompileStatic
import org.fidata.gradle.packer.template.internal.InterpolableValue
import org.fidata.gradle.packer.template.enums.Direction

@AutoClone(style = AutoCloneStyle.SIMPLE)
@InheritConstructors
@CompileStatic
class InterpolableDirection extends InterpolableValue<InterpolableString, Direction> {
  @Override
  protected Direction doInterpolatePrimitive() {
    rawValue.interpolate context
    Direction.forValue(rawValue.interpolatedValue)
  }
}
