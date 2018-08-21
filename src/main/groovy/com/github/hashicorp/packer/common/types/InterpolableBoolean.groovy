package com.github.hashicorp.packer.template.types

import groovy.transform.AutoClone
import groovy.transform.AutoCloneStyle
import groovy.transform.CompileStatic
import com.github.hashicorp.packer.common.types.internal.InterpolableValue
import com.fasterxml.jackson.annotation.JsonCreator

@AutoClone(style = AutoCloneStyle.SIMPLE)
@CompileStatic
class InterpolableBoolean extends InterpolableValue<Object, Boolean> {
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

  @Override
  protected Boolean doInterpolatePrimitive() {
    if (Boolean.isInstance(rawValue)) {
      (Boolean)rawValue
    } else if (InterpolableString.isInstance(rawValue)) {
      ((InterpolableString)rawValue).interpolate context
      ((InterpolableString)rawValue).interpolatedValue.toBoolean() // TOTEST
    } else {
      throw new IllegalStateException(sprintf('Invalid interpolable boolean raw value: %s', [rawValue]))
    }
  }
}
