package org.fidata.gradle.packer.template.types

import com.fasterxml.jackson.annotation.JsonValue
import go.time.DurationAdapter
import groovy.transform.CompileStatic
import org.fidata.gradle.packer.template.Context
import org.fidata.gradle.packer.template.internal.InterpolablePrimitive

import java.time.Duration

@CompileStatic
class InterpolableDuration extends InterpolablePrimitive<Duration> {
  @JsonValue
  String rawValue

  @Override
  protected Duration doInterpolatePrimitive(Context ctx) {
    DurationAdapter.parseDuration(ctx.interpolateString(rawValue))
  }
}
