package com.github.hashicorp.packer.engine.types

import groovy.transform.AutoClone
import groovy.transform.AutoCloneStyle
import groovy.transform.CompileStatic
import com.fasterxml.jackson.annotation.JsonCreator

@AutoClone(style = AutoCloneStyle.SIMPLE)
@CompileStatic
class InterpolableStringArray extends InterpolableValue<Object, ArrayList<String>> {
  static final class ArrayClass extends ArrayList<InterpolableString> {
  }

  // This constructor is required for Externalizable and AutoClone
  protected InterpolableStringArray() {
  }

  @JsonCreator
  InterpolableStringArray(ArrayClass rawValue) {
    super(rawValue)
  }

  @JsonCreator
  InterpolableStringArray(InterpolableString rawValue) {
    super(rawValue)
  }

  // @SuppressWarnings('ImplementationAsType')
  protected final ArrayList<String> doInterpolatePrimitive(ArrayClass rawValue) {
    new ArrayList<String>(rawValue.collect { InterpolableString it ->
      it.interpolate context
      it.interpolatedValue
    })
  }

  protected final ArrayList<String> doInterpolatePrimitive(InterpolableString rawValue) {
    rawValue.interpolate context
    new ArrayList<String>(rawValue.interpolatedValue.split(',').toList())
  }
}
