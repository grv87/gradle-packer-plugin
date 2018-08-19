package org.fidata.gradle.packer.template.types

import com.fasterxml.jackson.annotation.JsonCreator
import com.rits.cloning.Immutable
import groovy.transform.CompileStatic
import org.fidata.gradle.packer.template.Context
import org.fidata.gradle.packer.template.internal.InterpolableValue

@CompileStatic
@Immutable
class InterpolableStringArray extends InterpolableValue<Object, ArrayList<String>> {
  static class ArrayClass extends ArrayList<InterpolableString> {}

  @JsonCreator
  InterpolableStringArray(ArrayClass rawValue) {
    super(rawValue.asImmutable())
  }

  @JsonCreator
  InterpolableStringArray(InterpolableString rawValue) {
    super(rawValue)
  }

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
