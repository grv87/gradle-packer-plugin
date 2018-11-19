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
    UnsignedInteger.valueOf(rawValue.interpolatedValue(context))
  }

  // This is used to create instances with default values
  static final InterpolableUnsignedInteger withDefault(UnsignedInteger interpolatedValue) {
    withDefault(InterpolableUnsignedInteger, interpolatedValue)
  }
}
