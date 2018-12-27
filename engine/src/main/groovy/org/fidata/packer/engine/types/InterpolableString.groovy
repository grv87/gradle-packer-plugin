package org.fidata.packer.engine.types

import org.fidata.packer.engine.types.base.InterpolableValue
import org.fidata.packer.engine.types.base.SimpleInterpolableString
import com.github.hashicorp.packer.template.Context
import groovy.transform.CompileStatic
import groovy.transform.InheritConstructors
import groovy.transform.KnownImmutable

@CompileStatic
interface InterpolableString extends InterpolableValue<SimpleInterpolableString, String, InterpolableString> {
  @KnownImmutable
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

  @KnownImmutable
  @InheritConstructors
  final class AlreadyInterpolated extends InterpolableValue.AlreadyInterpolated<SimpleInterpolableString, String, InterpolableString> implements InterpolableString { }
}
