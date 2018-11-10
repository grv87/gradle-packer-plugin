package com.github.hashicorp.packer.engine.types

import groovy.transform.AutoClone
import groovy.transform.AutoCloneStyle
import groovy.transform.CompileStatic
import com.fasterxml.jackson.annotation.JsonCreator

@AutoClone(style = AutoCloneStyle.SIMPLE)
@CompileStatic
class InterpolableBoolean extends InterpolableValue<Object, Boolean> {
  // This constructor is required for Externalizable and AutoClone
  protected InterpolableBoolean() {
  }

  @JsonCreator
  InterpolableBoolean(Boolean rawValue) {
    super(rawValue)
  }

  @JsonCreator
  InterpolableBoolean(InterpolableString rawValue) {
    super(rawValue)
  }

  protected final Boolean doInterpolatePrimitive(Boolean rawValue) {
    rawValue
  }

  protected final Boolean doInterpolatePrimitive(InterpolableString rawValue) {
    rawValue.interpolate context
    rawValue.interpolatedValue // TOTEST
  }
}
