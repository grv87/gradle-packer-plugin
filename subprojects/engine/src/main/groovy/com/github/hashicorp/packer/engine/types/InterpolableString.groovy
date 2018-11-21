package com.github.hashicorp.packer.engine.types


import com.github.hashicorp.packer.template.Context
import com.google.common.base.Supplier
import groovy.transform.CompileStatic
import groovy.transform.InheritConstructors
import com.fasterxml.jackson.databind.annotation.JsonDeserialize

@JsonDeserialize(as = ReadWriteRawValue)
@CompileStatic
interface InterpolableString extends InterpolableValue<SimpleInterpolableString, String, InterpolableString> {
  @InheritConstructors
  class ReadWriteRawValue extends InterpolableValue.ReadWriteRawValue<SimpleInterpolableString, String, InterpolableString, ReadWriteRawValue, ReadWriteInitialized, AlreadyInterpolated> implements InterpolableString {
    protected final String doInterpolatePrimitive(Context context, SimpleInterpolableString rawValue) {
      rawValue.interpolate context
    }
  }

  @InheritConstructors
  class ReadWriteInitialized extends InterpolableValue.ReadWriteInitialized<SimpleInterpolableString, String, InterpolableString, ReadWriteRawValue, ReadWriteInitialized, AlreadyInterpolated> implements InterpolableString { }

  @InheritConstructors
    class AlreadyInterpolated extends InterpolableValue.AlreadyInterpolated<SimpleInterpolableString, String, InterpolableString, ReadWriteRawValue, ReadWriteInitialized, AlreadyInterpolated> implements InterpolableString { }

  static final class Utils extends InterpolableValue.Utils {
    // This is used to create instances with default values
    static final InterpolableString initWithDefault(InterpolableString interpolableValue, Supplier<String> defaultValueSupplier, Closure<Boolean> ignoreIf = null, Closure<String> postProcess = null) {
      initWithDefault ReadWriteRawValue, interpolableValue, defaultValueSupplier, ignoreIf, postProcess
    }

    // This is used to create instances with default values
    static final InterpolableString initWithDefault(InterpolableString interpolableValue, String defaultValue, Closure<Boolean> ignoreIf = null, Closure<String> postProcess = null) {
      initWithDefault ReadWriteRawValue, interpolableValue, defaultValue, ignoreIf, postProcess
    }
  }
}
