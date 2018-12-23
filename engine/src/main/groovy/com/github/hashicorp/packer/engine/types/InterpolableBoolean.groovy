package com.github.hashicorp.packer.engine.types

import com.github.hashicorp.packer.engine.types.base.InterpolableValue
import com.github.hashicorp.packer.engine.types.base.SimpleInterpolableString
import com.github.hashicorp.packer.template.Context
import groovy.transform.CompileStatic
import com.fasterxml.jackson.annotation.JsonCreator
import groovy.transform.InheritConstructors
import groovy.transform.KnownImmutable

@CompileStatic
interface InterpolableBoolean extends InterpolableValue<Object, Boolean, InterpolableBoolean> {
  @KnownImmutable
  final class ImmutableRaw extends InterpolableValue.ImmutableRaw<Object, Boolean, InterpolableBoolean, Interpolated, AlreadyInterpolated> implements InterpolableBoolean {
    ImmutableRaw() {
      super()
    }

    @JsonCreator(mode = JsonCreator.Mode.DELEGATING)
    ImmutableRaw(Boolean raw) {
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

    protected static final Boolean doInterpolatePrimitive(Context context, Boolean raw) {
      raw
    }

    protected static final Boolean doInterpolatePrimitive(Context context, SimpleInterpolableString raw) {
      raw.interpolate context // TOTEST
    }
  }

  final class Raw extends InterpolableValue.Raw<Object, Boolean, InterpolableBoolean, Interpolated, AlreadyInterpolated> implements InterpolableBoolean {
    Raw() {
      super()
    }

    @JsonCreator(mode = JsonCreator.Mode.DELEGATING)
    Raw(Boolean raw) {
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

    protected static final Boolean doInterpolatePrimitive(Context context, Boolean raw) {
      raw
    }

    protected static final Boolean doInterpolatePrimitive(Context context, SimpleInterpolableString raw) {
      raw.interpolate context // TOTEST
    }
  }

  @InheritConstructors
  final class Interpolated extends InterpolableValue.Interpolated<Object, Boolean, InterpolableBoolean, AlreadyInterpolated> implements InterpolableBoolean { }

  @KnownImmutable
  @InheritConstructors
  final class AlreadyInterpolated extends InterpolableValue.AlreadyInterpolated<Object, Boolean, InterpolableBoolean> implements InterpolableBoolean { }
}
