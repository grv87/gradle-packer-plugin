package com.github.hashicorp.packer.engine.types

import com.fasterxml.jackson.annotation.JsonCreator
import com.github.hashicorp.packer.engine.exceptions.InvalidRawValueClass
import com.google.common.primitives.UnsignedInteger
import groovy.transform.AutoClone
import groovy.transform.AutoCloneStyle
import groovy.transform.CompileStatic

@AutoClone(style = AutoCloneStyle.SIMPLE)
@CompileStatic
class InterpolableUnsignedInteger extends InterpolableValue<Object, UnsignedInteger> {
  protected InterpolableUnsignedInteger() {
  }

  @JsonCreator
  InterpolableUnsignedInteger(Integer rawValue) {
    super(rawValue)
  }

  @JsonCreator
  InterpolableUnsignedInteger(InterpolableString rawValue) {
    super(rawValue)
  }

  @Override
  protected UnsignedInteger doInterpolatePrimitive() {
    if (UnsignedInteger.isInstance(rawValue)) {
      (UnsignedInteger)rawValue
    } else if (InterpolableString.isInstance(rawValue)) {
      ((InterpolableString)rawValue).interpolate context
      UnsignedInteger.valueOf(((InterpolableString)rawValue).interpolatedValue)
    } else {
      throw new InvalidRawValueClass(rawValue)
    }
  }
}
