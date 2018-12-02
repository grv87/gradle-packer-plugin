package com.github.hashicorp.packer.engine.types

import static go.time.DurationAdapter.parseDuration
import com.github.hashicorp.packer.template.Context
import groovy.transform.InheritConstructors
import groovy.transform.CompileStatic
import com.fasterxml.jackson.annotation.JsonCreator
import java.time.Duration

@CompileStatic
interface InterpolableDuration extends InterpolableValue<Object, Duration, InterpolableDuration> {
  final class ImmutableRaw extends InterpolableValue.ImmutableRaw<Object, Duration, InterpolableDuration, Interpolated, AlreadyInterpolated> implements InterpolableDuration {
    ImmutableRaw() {
      super()
    }

    @JsonCreator
    ImmutableRaw(Duration raw) {
      super(raw)
    }

    @JsonCreator
    ImmutableRaw(SimpleInterpolableString raw) {
      super(raw)
    }

    protected static final Duration doInterpolatePrimitive(Context context, Duration raw) {
      raw
    }

    protected static final Duration doInterpolatePrimitive(Context context, SimpleInterpolableString raw) {
      parseDuration(raw.interpolate(context))
    }
  }

  final class Raw extends InterpolableValue.Raw<Object, Duration, InterpolableDuration, Interpolated, AlreadyInterpolated> implements InterpolableDuration {
    Raw() {
      super()
    }

    @JsonCreator
    Raw(Duration raw) {
      super(raw)
    }

    @JsonCreator
    Raw(SimpleInterpolableString raw) {
      super(raw)
    }

    protected static final Duration doInterpolatePrimitive(Context context, Duration raw) {
      raw
    }

    protected static final Duration doInterpolatePrimitive(Context context, SimpleInterpolableString raw) {
      parseDuration(raw.interpolate(context))
    }
  }

  @InheritConstructors
  final class Interpolated extends InterpolableValue.Interpolated<Object, Duration, InterpolableDuration, AlreadyInterpolated> implements InterpolableDuration { }

  @InheritConstructors
  final class AlreadyInterpolated extends InterpolableValue.AlreadyInterpolated<Object, Duration, InterpolableDuration> implements InterpolableDuration { }
}
