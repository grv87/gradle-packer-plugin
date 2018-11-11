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
      it.interpolatedValue(context)
    })
  }

  protected final ArrayList<String> doInterpolatePrimitive(InterpolableString rawValue) {
    new ArrayList<String>(rawValue.interpolatedValue(context).split(',').toList())
  }
}
