package com.github.hashicorp.packer.engine.types

import com.fasterxml.jackson.annotation.JsonCreator
import groovy.transform.AutoClone
import groovy.transform.AutoCloneStyle
import groovy.transform.CompileStatic

@AutoClone(style = AutoCloneStyle.SIMPLE)
@CompileStatic
class InterpolableLong extends InterpolableValue<Object, Long> {
  // This constructor is required for Externalizable and AutoClone
  protected InterpolableLong() {
  }

  @JsonCreator
  InterpolableLong(Long rawValue) {
    super(rawValue)
  }

  @JsonCreator
  InterpolableLong(InterpolableString rawValue) {
    super(rawValue)
  }

  protected final Long doInterpolatePrimitive(Long rawValue) {
    rawValue
  }

  protected final Long doInterpolatePrimitive(InterpolableString rawValue) {
    rawValue.interpolate context
    rawValue.interpolatedValue.toLong()
  }
}
