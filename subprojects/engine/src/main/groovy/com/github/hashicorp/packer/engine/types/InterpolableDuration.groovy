package com.github.hashicorp.packer.engine.types

import com.github.hashicorp.packer.engine.types.base.InterpolableValue
import com.github.hashicorp.packer.engine.types.base.SimpleInterpolableString
import groovy.transform.KnownImmutable

import static go.time.DurationAdapter.parseDuration
import com.github.hashicorp.packer.template.Context
import groovy.transform.InheritConstructors
import groovy.transform.CompileStatic
import com.fasterxml.jackson.annotation.JsonCreator
import java.time.Duration

@CompileStatic
interface InterpolableDuration extends InterpolableValue<Object, Duration, InterpolableDuration> {
  @KnownImmutable
  final class ImmutableRaw extends InterpolableValue.ImmutableRaw<Object, Duration, InterpolableDuration, Interpolated, AlreadyInterpolated> implements InterpolableDuration {
    ImmutableRaw() {
      super()
    }

    @JsonCreator(mode = JsonCreator.Mode.DELEGATING)
    ImmutableRaw(Duration raw) {
      super(raw)
    }

    @JsonCreator(mode = JsonCreator.Mode.DELEGATING)
    /*
     * WORKAROUND:
     * Can't use argument of type SimpleInterpolableString
     * since Jackson doesn't work correctly with nested value classes
     * <grv87 2018-12-20>
     */
    ImmutableRaw(String raw) {
      super(new SimpleInterpolableString(raw))
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

    @JsonCreator(mode = JsonCreator.Mode.DELEGATING)
    Raw(Duration raw) {
      super(raw)
    }

    @JsonCreator(mode = JsonCreator.Mode.DELEGATING)
    /*
     * WORKAROUND:
     * Can't use argument of type SimpleInterpolableString
     * since Jackson doesn't work correctly with nested value classes
     * <grv87 2018-12-20>
     */
    Raw(String raw) {
      super(new SimpleInterpolableString(raw))
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

  @KnownImmutable
  @InheritConstructors
  final class AlreadyInterpolated extends InterpolableValue.AlreadyInterpolated<Object, Duration, InterpolableDuration> implements InterpolableDuration { }
}
