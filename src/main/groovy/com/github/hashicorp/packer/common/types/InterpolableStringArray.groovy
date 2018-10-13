package com.github.hashicorp.packer.template.types

import groovy.transform.AutoClone
import groovy.transform.AutoCloneStyle
import groovy.transform.CompileStatic
import com.github.hashicorp.packer.common.types.internal.InterpolableValue
import com.fasterxml.jackson.annotation.JsonCreator

@AutoClone(style = AutoCloneStyle.SIMPLE)
@CompileStatic
class InterpolableStringArray extends InterpolableValue<Object, ArrayList<String>> {
  static class ArrayClass extends ArrayList<InterpolableString> {
  }

  protected InterpolableStringArray() {
  }

  @JsonCreator
  InterpolableStringArray(ArrayClass rawValue) {
    super(rawValue.asImmutable())
  }

  @JsonCreator
  InterpolableStringArray(InterpolableString rawValue) {
    super(rawValue)
  }

  @SuppressWarnings('ImplementationAsType')
  @Override
  protected ArrayList<String> doInterpolatePrimitive() {
    if (ArrayClass.isInstance(rawValue)) {
      new ArrayList<String>(((ArrayClass)rawValue).collect { it.interpolate context; it.interpolatedValue })
    } else if (InterpolableString.isInstance(rawValue)) {
      ((InterpolableString)rawValue).interpolate context
      new ArrayList<String>(((InterpolableString)rawValue).interpolatedValue.split(',').toList())
    } else {
      throw new IllegalStateException(sprintf('Invalid interpolable string array raw value: %s', [rawValue]))
    }
  }
}