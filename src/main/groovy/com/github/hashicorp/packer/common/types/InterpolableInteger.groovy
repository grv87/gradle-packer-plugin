package com.github.hashicorp.packer.template.types

import groovy.transform.AutoClone
import groovy.transform.AutoCloneStyle
import groovy.transform.CompileStatic
import com.github.hashicorp.packer.common.types.internal.InterpolableValue
import com.fasterxml.jackson.annotation.JsonCreator

@AutoClone(style = AutoCloneStyle.SIMPLE)
@CompileStatic
class InterpolableInteger extends InterpolableValue<Object, Integer> {
  protected InterpolableInteger() {
  }

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
      ((InterpolableString)rawValue).interpolate context
      ((InterpolableString)rawValue).interpolatedValue.toInteger()
    } else {
      throw new IllegalStateException(sprintf('Invalid interpolable integer raw value: %s', [rawValue]))
    }
  }
}
