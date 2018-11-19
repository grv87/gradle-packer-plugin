package com.github.hashicorp.packer.engine.types

import com.google.common.collect.ImmutableList
import groovy.transform.CompileStatic
import com.fasterxml.jackson.annotation.JsonCreator

@CompileStatic
class InterpolableStringArray extends InterpolableValue<Object, ArrayList<String>> {
  static final class ArrayClass extends ImmutableList<InterpolableString> {
  }

  // This constructor is required for Externalizable
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
    new ArrayList<String>(rawValue*.interpolatedValue(context))
  }

  protected final ArrayList<String> doInterpolatePrimitive(InterpolableString rawValue) {
    new ArrayList<String>(rawValue.interpolatedValue(context).split(',').toList())
  }
}
