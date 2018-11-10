package com.github.hashicorp.packer.engine.types

import com.fasterxml.jackson.annotation.JsonCreator
import com.github.hashicorp.packer.engine.exceptions.InvalidRawValueClass
import com.google.common.primitives.UnsignedInteger
import groovy.transform.AutoClone
import groovy.transform.AutoCloneStyle
import groovy.transform.CompileStatic

@AutoClone(style = AutoCloneStyle.SIMPLE)
@CompileStatic
// @KnownImmutable // TODO: Groovy 2.5
class InterpolableUnsignedInteger extends InterpolableValue<Object, UnsignedInteger> {
  // This constructor is required for Externalizable and AutoClone
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

  protected final UnsignedInteger doInterpolatePrimitive(UnsignedInteger rawValue) {
    rawValue
  }

  protected final UnsignedInteger doInterpolatePrimitive(InterpolableString rawValue) {
    rawValue.interpolate context
    UnsignedInteger.valueOf(rawValue.interpolatedValue)
  }
}
