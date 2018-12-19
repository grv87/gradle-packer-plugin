package com.github.hashicorp.packer.engine.types

import com.fasterxml.jackson.annotation.JsonCreator
import com.github.hashicorp.packer.engine.types.base.InterpolableValue
import com.github.hashicorp.packer.engine.types.base.SimpleInterpolableString
import com.github.hashicorp.packer.template.Context
import groovy.transform.CompileStatic
import groovy.transform.InheritConstructors

@CompileStatic
interface InterpolableLong extends InterpolableValue<Object, Long, InterpolableLong> {
  final class ImmutableRaw extends InterpolableValue.ImmutableRaw<Object, Long, InterpolableLong, Interpolated, AlreadyInterpolated> implements InterpolableLong {
    ImmutableRaw() {
      super()
    }

    @JsonCreator(mode = JsonCreator.Mode.DELEGATING)
    ImmutableRaw(Long raw) {
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

    protected static final Long doInterpolatePrimitive(Context context, Long raw) {
      raw
    }

    protected static final Long doInterpolatePrimitive(Context context, SimpleInterpolableString raw) {
      raw.interpolate(context).toLong()
    }
  }

  final class Raw extends InterpolableValue.Raw<Object, Long, InterpolableLong, Interpolated, AlreadyInterpolated> implements InterpolableLong {
    Raw() {
      super()
    }

    @JsonCreator(mode = JsonCreator.Mode.DELEGATING)
    Raw(Long raw) {
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

    protected static final Long doInterpolatePrimitive(Context context, Long raw) {
      raw
    }

    protected static final Long doInterpolatePrimitive(Context context, SimpleInterpolableString raw) {
      raw.interpolate(context).toLong()
    }
  }

  @InheritConstructors
  final class Interpolated extends InterpolableValue.Interpolated<Object, Long, InterpolableLong, AlreadyInterpolated> implements InterpolableLong { }

  @InheritConstructors
  final class AlreadyInterpolated extends InterpolableValue.AlreadyInterpolated<Object, Long, InterpolableLong> implements InterpolableLong { }
}
