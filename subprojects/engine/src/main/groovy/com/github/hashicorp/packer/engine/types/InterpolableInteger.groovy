package com.github.hashicorp.packer.engine.types

import com.github.hashicorp.packer.engine.exceptions.InvalidRawValueClass
import groovy.transform.AutoClone
import groovy.transform.AutoCloneStyle
import groovy.transform.CompileStatic
import com.fasterxml.jackson.annotation.JsonCreator

@AutoClone(style = AutoCloneStyle.SIMPLE)
@CompileStatic
// @KnownImmutable // TODO: Groovy 2.5
class InterpolableInteger extends InterpolableValue<Object, Integer> {
  // This constructor is required for Externalizable and AutoClone
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
  protected final Integer doInterpolatePrimitive() {
    if (Integer.isInstance(rawValue)) {
      (Integer)rawValue
    } else if (InterpolableString.isInstance(rawValue)) {
      ((InterpolableString)rawValue).interpolate context
      ((InterpolableString)rawValue).interpolatedValue.toInteger()
    } else {
      throw new InvalidRawValueClass(rawValue)
    }
  }
}
