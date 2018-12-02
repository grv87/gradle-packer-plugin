package com.github.hashicorp.packer.engine.types

import com.github.hashicorp.packer.template.Context
import groovy.transform.CompileStatic
import com.fasterxml.jackson.annotation.JsonCreator
import groovy.transform.InheritConstructors

@CompileStatic
interface InterpolableBoolean extends InterpolableValue<Object, Boolean, InterpolableBoolean> {
  final class ImmutableRaw extends InterpolableValue.ImmutableRaw<Object, Boolean, InterpolableBoolean, Interpolated, AlreadyInterpolated> implements InterpolableBoolean {
    ImmutableRaw() {
      super()
    }

    @JsonCreator
    ImmutableRaw(Boolean raw) {
      super(raw)
    }

    @JsonCreator
    ImmutableRaw(SimpleInterpolableString raw) {
      super(raw)
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

    @JsonCreator
    Raw(Boolean raw) {
      super(raw)
    }

    @JsonCreator
    Raw(SimpleInterpolableString raw) {
      super(raw)
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

  @InheritConstructors
  final class AlreadyInterpolated extends InterpolableValue.AlreadyInterpolated<Object, Boolean, InterpolableBoolean> implements InterpolableBoolean { }
}
