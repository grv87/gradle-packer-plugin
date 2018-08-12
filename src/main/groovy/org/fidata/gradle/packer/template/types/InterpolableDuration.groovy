package org.fidata.gradle.packer.template.types

import static go.time.DurationAdapter.parseDuration
import com.fasterxml.jackson.annotation.JsonCreator
import groovy.transform.CompileStatic
import org.fidata.gradle.packer.template.Context
import org.fidata.gradle.packer.template.internal.InterpolableSinglePrimitive
import java.time.Duration

@CompileStatic
class InterpolableDuration extends InterpolableSinglePrimitive<InterpolableString, Duration> {
  @JsonCreator
  InterpolableDuration(InterpolableString rawValue) {
    super(rawValue)
  }

  @Override
  protected Duration doInterpolatePrimitive(Context ctx) {
    rawValue.interpolate ctx
    parseDuration(rawValue.interpolatedValue)
  }
}
