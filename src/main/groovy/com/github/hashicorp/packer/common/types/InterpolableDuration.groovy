package com.github.hashicorp.packer.template.types

import static go.time.DurationAdapter.parseDuration
import groovy.transform.AutoClone
import groovy.transform.AutoCloneStyle
import groovy.transform.InheritConstructors
import groovy.transform.CompileStatic
import com.github.hashicorp.packer.common.types.internal.InterpolableValue
import java.time.Duration

@AutoClone(style = AutoCloneStyle.SIMPLE)
@InheritConstructors
@CompileStatic
class InterpolableDuration extends InterpolableValue<InterpolableString, Duration> {
  @Override
  protected Duration doInterpolatePrimitive() {
    rawValue.interpolate context
    parseDuration(rawValue.interpolatedValue)
  }
}