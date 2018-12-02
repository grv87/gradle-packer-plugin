package com.github.hashicorp.packer.engine.types

import com.fasterxml.jackson.annotation.JsonCreator
import com.github.hashicorp.packer.template.Context
import groovy.transform.CompileStatic
import groovy.transform.InheritConstructors

@CompileStatic
interface InterpolableLong extends InterpolableValue<Object, Long, InterpolableLong> {
  final class ImmutableRaw extends InterpolableValue.ImmutableRaw<Object, Long, InterpolableLong, Interpolated, AlreadyInterpolated> implements InterpolableLong {
    ImmutableRaw() {
      super()
    }

    @JsonCreator
    ImmutableRaw(Long raw) {
      super(raw)
    }

    @JsonCreator
    ImmutableRaw(SimpleInterpolableString raw) {
      super(raw)
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

    @JsonCreator
    Raw(Long raw) {
      super(raw)
    }

    @JsonCreator
    Raw(SimpleInterpolableString raw) {
      super(raw)
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
