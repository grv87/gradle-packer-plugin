package com.github.hashicorp.packer.engine.types

import com.fasterxml.jackson.annotation.JsonCreator
import com.google.common.primitives.UnsignedInteger
import groovy.transform.CompileStatic

@CompileStatic
class InterpolableUnsignedInteger extends InterpolableValue<Object, UnsignedInteger> {
  // This constructor is required for Externalizable
  protected InterpolableUnsignedInteger() {
  }

  @JsonCreator
  InterpolableUnsignedInteger(Integer raw) {
    super(raw)
  }

  @JsonCreator
  InterpolableUnsignedInteger(InterpolableString raw) {
    super(raw)
  }

  protected final UnsignedInteger doInterpolatePrimitive(UnsignedInteger raw) {
    rawValue
  }

  protected final UnsignedInteger doInterpolatePrimitive(InterpolableString raw) {
    UnsignedInteger.valueOf(raw.interpolatedValue(context))
  }

  // This is used to create instances with default values
  static final InterpolableUnsignedInteger withDefault(UnsignedInteger interpolatedValue) {
    withDefault(InterpolableUnsignedInteger, interpolatedValue)
  }
}
