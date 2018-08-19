package org.fidata.gradle.packer.template.types

import com.fasterxml.jackson.annotation.JsonCreator
import com.rits.cloning.Immutable
import groovy.transform.CompileStatic
import org.fidata.gradle.packer.template.Context
import org.fidata.gradle.packer.template.internal.InterpolableValue

@CompileStatic
@Immutable
class InterpolableInteger extends InterpolableValue<Object, Integer> {
  @JsonCreator
  InterpolableInteger(Integer rawValue) {
    super(rawValue)
  }

  @JsonCreator
  InterpolableInteger(InterpolableString rawValue) {
    super(rawValue)
  }

  @Override
  protected Integer doInterpolatePrimitive() {
    if (Integer.isInstance(rawValue)) {
      (Integer)rawValue
    } else if (InterpolableString.isInstance(rawValue)) {
      ((InterpolableString)rawValue).interpolate ctx
      ((InterpolableString)rawValue).interpolatedValue.toInteger()
    } else {
      throw new IllegalStateException(sprintf('Invalid interpolable integer raw value: %s', [rawValue]))
    }
  }
}
