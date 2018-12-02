package com.github.hashicorp.packer.engine.types

import com.fasterxml.jackson.annotation.JsonCreator
import com.github.hashicorp.packer.template.Context
import com.google.common.primitives.UnsignedInteger
import groovy.transform.CompileStatic
import groovy.transform.InheritConstructors

@CompileStatic
interface InterpolableUnsignedInteger extends InterpolableValue<Object, UnsignedInteger, InterpolableUnsignedInteger> {
  final class ImmutableRaw extends InterpolableValue.ImmutableRaw<Object, UnsignedInteger, InterpolableUnsignedInteger, Interpolated, AlreadyInterpolated> implements InterpolableUnsignedInteger {
    ImmutableRaw() {
      super()
    }

    @JsonCreator
    ImmutableRaw(UnsignedInteger raw) {
      super(raw)
    }

    @JsonCreator
    ImmutableRaw(SimpleInterpolableString raw) {
      super(raw)
    }

    protected static final UnsignedInteger doInterpolatePrimitive(Context context, UnsignedInteger raw) {
      raw
    }

    protected static final UnsignedInteger doInterpolatePrimitive(Context context, SimpleInterpolableString raw) {
      UnsignedInteger.valueOf(raw.interpolate(context))
    }
  }

  final class Raw extends InterpolableValue.Raw<Object, UnsignedInteger, InterpolableUnsignedInteger, Interpolated, AlreadyInterpolated> implements InterpolableUnsignedInteger {
    Raw() {
      super()
    }

    @JsonCreator
    Raw(UnsignedInteger raw) {
      super(raw)
    }

    @JsonCreator
    Raw(SimpleInterpolableString raw) {
      super(raw)
    }

    protected static final UnsignedInteger doInterpolatePrimitive(Context context, UnsignedInteger raw) {
      raw
    }

    protected static final UnsignedInteger doInterpolatePrimitive(Context context, SimpleInterpolableString raw) {
      UnsignedInteger.valueOf(raw.interpolate(context))
    }
  }

  @InheritConstructors
  final class Interpolated extends InterpolableValue.Interpolated<Object, UnsignedInteger, InterpolableUnsignedInteger, AlreadyInterpolated> implements InterpolableUnsignedInteger { }

  @InheritConstructors
  final class AlreadyInterpolated extends InterpolableValue.AlreadyInterpolated<Object, UnsignedInteger, InterpolableUnsignedInteger> implements InterpolableUnsignedInteger { }
}
