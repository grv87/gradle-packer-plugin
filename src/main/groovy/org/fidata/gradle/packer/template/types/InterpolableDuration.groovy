package org.fidata.gradle.packer.template.types

import static go.time.DurationAdapter.parseDuration
import com.fasterxml.jackson.annotation.JsonCreator
import groovy.transform.CompileStatic
import org.fidata.gradle.packer.template.Context
import org.fidata.gradle.packer.template.internal.InterpolableSinglePrimitive
import java.time.Duration

@CompileStatic
class InterpolableDuration extends InterpolableSinglePrimitive<String, Duration> {
  @JsonCreator
  InterpolableDuration(String rawValue) {
    super(rawValue)
  }

  @Override
  protected Duration doInterpolatePrimitive(Context ctx) {
    parseDuration(ctx.interpolateString(rawValue))
  }
}
