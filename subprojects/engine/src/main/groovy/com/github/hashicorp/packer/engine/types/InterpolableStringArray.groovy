package com.github.hashicorp.packer.engine.types

import com.github.hashicorp.packer.engine.exceptions.InvalidRawValueClass
import groovy.transform.AutoClone
import groovy.transform.AutoCloneStyle
import groovy.transform.CompileStatic
import com.fasterxml.jackson.annotation.JsonCreator

@AutoClone(style = AutoCloneStyle.SIMPLE)
@CompileStatic
class InterpolableStringArray extends InterpolableValue<Object, ArrayList<String>> {
  static class ArrayClass extends ArrayList<InterpolableString> {
  }

  // This constructor is required for Externalizable and AutoClone
  protected InterpolableStringArray() {
  }

  @JsonCreator
  InterpolableStringArray(ArrayClass rawValue) {
    super(rawValue.asImmutable()) // TODO: no sense to make rawValue immutable since it is not final anyway
  }

  @JsonCreator
  InterpolableStringArray(InterpolableString rawValue) {
    super(rawValue)
  }

  @SuppressWarnings('ImplementationAsType')
  @Override
  protected final ArrayList<String> doInterpolatePrimitive() {
    if (ArrayClass.isInstance(rawValue)) {
      new ArrayList<String>(((ArrayClass)rawValue).collect { it.interpolate context; it.interpolatedValue })
    } else if (InterpolableString.isInstance(rawValue)) {
      ((InterpolableString)rawValue).interpolate context
      new ArrayList<String>(((InterpolableString)rawValue).interpolatedValue.split(',').toList())
    } else {
      throw new InvalidRawValueClass(rawValue)
    }
  }
}
