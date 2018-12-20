package com.github.hashicorp.packer.engine.types

import com.fasterxml.jackson.annotation.JsonCreator
import com.github.hashicorp.packer.engine.types.base.InterpolableValue
import com.github.hashicorp.packer.engine.types.base.SimpleInterpolableString
import com.github.hashicorp.packer.template.Context
import com.google.common.primitives.UnsignedInteger
import groovy.transform.CompileStatic
import groovy.transform.InheritConstructors
import groovy.transform.KnownImmutable

@CompileStatic
interface InterpolableUnsignedInteger extends InterpolableValue<Object, UnsignedInteger, InterpolableUnsignedInteger> {
  @KnownImmutable
  final class ImmutableRaw extends InterpolableValue.ImmutableRaw<Object, UnsignedInteger, InterpolableUnsignedInteger, Interpolated, AlreadyInterpolated> implements InterpolableUnsignedInteger {
    ImmutableRaw() {
      super()
    }

    @JsonCreator(mode = JsonCreator.Mode.DELEGATING)
    ImmutableRaw(UnsignedInteger raw) {
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

    @JsonCreator(mode = JsonCreator.Mode.DELEGATING)
    Raw(UnsignedInteger raw) {
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

    protected static final UnsignedInteger doInterpolatePrimitive(Context context, UnsignedInteger raw) {
      raw
    }

    protected static final UnsignedInteger doInterpolatePrimitive(Context context, SimpleInterpolableString raw) {
      UnsignedInteger.valueOf(raw.interpolate(context))
    }
  }

  @InheritConstructors
  final class Interpolated extends InterpolableValue.Interpolated<Object, UnsignedInteger, InterpolableUnsignedInteger, AlreadyInterpolated> implements InterpolableUnsignedInteger { }

  @KnownImmutable
  @InheritConstructors
  final class AlreadyInterpolated extends InterpolableValue.AlreadyInterpolated<Object, UnsignedInteger, InterpolableUnsignedInteger> implements InterpolableUnsignedInteger { }
}
