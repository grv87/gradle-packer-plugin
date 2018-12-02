package com.github.hashicorp.packer.engine.types

import com.fasterxml.jackson.annotation.JsonCreator
import com.github.hashicorp.packer.template.Context
import groovy.transform.CompileStatic
import groovy.transform.InheritConstructors

@CompileStatic
interface InterpolableInteger extends InterpolableValue<Object, Integer, InterpolableInteger> {
  final class ImmutableRaw extends InterpolableValue.ImmutableRaw<Object, Integer, InterpolableInteger, Interpolated, AlreadyInterpolated> implements InterpolableInteger {
    ImmutableRaw() {
      super()
    }

    @JsonCreator
    ImmutableRaw(Integer raw) {
      super(raw)
    }

    @JsonCreator
    ImmutableRaw(SimpleInterpolableString raw) {
      super(raw)
    }

    protected static final Integer doInterpolatePrimitive(Context context, Integer raw) {
      raw
    }

    protected static final Integer doInterpolatePrimitive(Context context, SimpleInterpolableString raw) {
      raw.interpolate(context).toInteger()
    }
  }

  final class Raw extends InterpolableValue.Raw<Object, Integer, InterpolableInteger, Interpolated, AlreadyInterpolated> implements InterpolableInteger {
    Raw() {
      super()
    }

    @JsonCreator
    Raw(Integer raw) {
      super(raw)
    }

    @JsonCreator
    Raw(SimpleInterpolableString raw) {
      super(raw)
    }

    protected static final Integer doInterpolatePrimitive(Context context, Integer raw) {
      raw
    }

    protected static final Integer doInterpolatePrimitive(Context context, SimpleInterpolableString raw) {
      raw.interpolate(context).toInteger()
    }
  }

  @InheritConstructors
  final class Interpolated extends InterpolableValue.Interpolated<Object, Integer, InterpolableInteger, AlreadyInterpolated> implements InterpolableInteger { }

  @InheritConstructors
  final class AlreadyInterpolated extends InterpolableValue.AlreadyInterpolated<Object, Integer, InterpolableInteger> implements InterpolableInteger { }
}
