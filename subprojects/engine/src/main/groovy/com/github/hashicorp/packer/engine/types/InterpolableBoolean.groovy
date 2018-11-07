package com.github.hashicorp.packer.engine.types

import com.github.hashicorp.packer.engine.exceptions.InvalidRawValueClass
import groovy.transform.AutoClone
import groovy.transform.AutoCloneStyle
import groovy.transform.CompileStatic
import com.fasterxml.jackson.annotation.JsonCreator

@AutoClone(style = AutoCloneStyle.SIMPLE)
@CompileStatic
// @KnownImmutable // TODO: Groovy 2.5
class InterpolableBoolean extends InterpolableValue<Object, Boolean> {
  // This constructor is required for Externalizable
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
      ((InterpolableString)rawValue).interpolatedValue.asBoolean() // TOTEST
    } else {
      throw new InvalidRawValueClass(rawValue)
    }
  }
}
