package org.fidata.gradle.packer.template.types

import com.rits.cloning.Immutable

import static go.time.DurationAdapter.parseDuration
import com.fasterxml.jackson.annotation.JsonCreator
import groovy.transform.CompileStatic
import org.fidata.gradle.packer.template.Context
import org.fidata.gradle.packer.template.internal.InterpolableValue
import java.time.Duration

@CompileStatic
@Immutable
class InterpolableDuration extends InterpolableValue<InterpolableString, Duration> {
  @JsonCreator
  InterpolableDuration(InterpolableString rawValue) {
    super(rawValue)
  }

  @Override
  protected Duration doInterpolatePrimitive() {
    rawValue.interpolate ctx
    parseDuration(rawValue.interpolatedValue)
  }
}
