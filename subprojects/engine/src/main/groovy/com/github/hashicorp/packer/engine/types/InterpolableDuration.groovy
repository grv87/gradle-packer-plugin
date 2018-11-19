package com.github.hashicorp.packer.engine.types

import static go.time.DurationAdapter.parseDuration
import groovy.transform.InheritConstructors
import groovy.transform.CompileStatic
import java.time.Duration

@InheritConstructors
@CompileStatic
class InterpolableDuration extends InterpolableValue<InterpolableString, Duration> {
  protected final Duration doInterpolatePrimitive(InterpolableString rawValue) {
    parseDuration(rawValue.interpolatedValue(context))
  }

  // This is used to create instances with default values
  static final InterpolableDuration withDefault(Duration interpolatedValue) {
    withDefault(InterpolableDuration, interpolatedValue)
  }
}
