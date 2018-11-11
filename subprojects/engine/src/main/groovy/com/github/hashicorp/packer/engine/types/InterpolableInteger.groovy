package com.github.hashicorp.packer.engine.types

import groovy.transform.AutoClone
import groovy.transform.AutoCloneStyle
import groovy.transform.CompileStatic
import com.fasterxml.jackson.annotation.JsonCreator

@AutoClone(style = AutoCloneStyle.SIMPLE)
@CompileStatic
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

  protected final Integer doInterpolatePrimitive(Integer rawValue) {
    rawValue
  }

  protected final Integer doInterpolatePrimitive(InterpolableString rawValue) {
    rawValue.interpolatedValue(context).toInteger()
  }

  // This is used to create instances with default values
  static final InterpolableInteger withDefault(Integer interpolatedValue) {
    withDefault(InterpolableInteger, interpolatedValue)
  }
}
