package com.github.hashicorp.packer.engine.types

import com.github.hashicorp.packer.engine.types.base.InterpolableValue
import com.github.hashicorp.packer.engine.types.base.SimpleInterpolableString
import com.github.hashicorp.packer.template.Context
import groovy.transform.CompileStatic
import groovy.transform.InheritConstructors

@CompileStatic
interface InterpolableString extends InterpolableValue<SimpleInterpolableString, String, InterpolableString> {
  @InheritConstructors
  final class ImmutableRaw extends InterpolableValue.ImmutableRaw<SimpleInterpolableString, String, InterpolableString, Interpolated, AlreadyInterpolated> implements InterpolableString {
    protected static final String doInterpolatePrimitive(Context context, SimpleInterpolableString raw) {
      raw.interpolate context
    }
  }

  @InheritConstructors
  final class Raw extends InterpolableValue.Raw<SimpleInterpolableString, String, InterpolableString, Interpolated, AlreadyInterpolated> implements InterpolableString {
    protected static final String doInterpolatePrimitive(Context context, SimpleInterpolableString raw) {
      raw.interpolate context
    }
  }

  @InheritConstructors
  final class Interpolated extends InterpolableValue.Interpolated<SimpleInterpolableString, String, InterpolableString, AlreadyInterpolated> implements InterpolableString { }

  @InheritConstructors
  final class AlreadyInterpolated extends InterpolableValue.AlreadyInterpolated<SimpleInterpolableString, String, InterpolableString> implements InterpolableString { }
}
