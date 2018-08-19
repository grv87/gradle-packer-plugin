package org.fidata.gradle.packer.template.types

import com.fasterxml.jackson.annotation.JsonCreator
import com.rits.cloning.Immutable
import groovy.transform.CompileStatic
import org.fidata.gradle.packer.template.Context
import org.fidata.gradle.packer.template.internal.InterpolableValue

@CompileStatic
@Immutable
class InterpolableBoolean extends InterpolableValue<Object, Boolean> {
  @JsonCreator
  InterpolableBoolean(Boolean rawValue) {
    super(rawValue)
  }

  @JsonCreator
  InterpolableBoolean(InterpolableString rawValue) {
    super(rawValue)
  }

  @Override
  protected Boolean doInterpolatePrimitive() {
    if (Boolean.isInstance(rawValue)) {
      (Boolean)rawValue
    } else if (InterpolableString.isInstance(rawValue)) {
      ((InterpolableString)rawValue).interpolate context
      ((InterpolableString)rawValue).interpolatedValue.toBoolean() // TOTEST
    } else {
      throw new IllegalStateException(sprintf('Invalid interpolable boolean raw value: %s', [rawValue]))
    }
  }
}
