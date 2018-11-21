package com.github.hashicorp.packer.engine.types


import com.github.hashicorp.packer.template.Context
import com.google.common.base.Supplier
import groovy.transform.CompileStatic
import groovy.transform.InheritConstructors
import com.fasterxml.jackson.databind.annotation.JsonDeserialize

@JsonDeserialize(as = RawValue)
@CompileStatic
interface InterpolableString extends InterpolableValue<SimpleInterpolableString, String, InterpolableString> {
  @InheritConstructors
  class RawValue extends InterpolableValue.RawValue<SimpleInterpolableString, String, InterpolableString, AlreadyInterpolated, Initialized> implements InterpolableString {
    protected final String doInterpolatePrimitive(Context context, SimpleInterpolableString rawValue) {
      rawValue.interpolate context
    }
  }

  @InheritConstructors
  class Initialized extends InterpolableValue.Initialized<SimpleInterpolableString, String, InterpolableString, AlreadyInterpolated, Initialized> implements InterpolableString { }

  @InheritConstructors
  class AlreadyInterpolated extends InterpolableValue.AlreadyInterpolated<SimpleInterpolableString, String, InterpolableString> implements InterpolableString { }

  static final class Utils extends InterpolableValue.Utils {
    // This is used to create instances with default values
    static final InterpolableString initWithDefault(InterpolableString interpolableValue, Supplier<String> defaultValueSupplier, Closure<Boolean> ignoreIf = null, Closure<String> postProcess = null) {
      initWithDefault RawValue, interpolableValue, defaultValueSupplier, ignoreIf, postProcess
    }

    // This is used to create instances with default values
    static final InterpolableString initWithDefault(InterpolableString interpolableValue, String defaultValue, Closure<Boolean> ignoreIf = null, Closure<String> postProcess = null) {
      initWithDefault RawValue, interpolableValue, defaultValue, ignoreIf, postProcess
    }
  }
}
