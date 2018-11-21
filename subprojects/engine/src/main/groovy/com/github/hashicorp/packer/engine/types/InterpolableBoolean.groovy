package com.github.hashicorp.packer.engine.types

import com.github.hashicorp.packer.template.Context
import com.google.common.base.Supplier
import groovy.transform.CompileStatic
import com.fasterxml.jackson.annotation.JsonCreator
import groovy.transform.InheritConstructors
import com.fasterxml.jackson.databind.annotation.JsonDeserialize

@JsonDeserialize(as = RawValue)
@CompileStatic
interface InterpolableBoolean extends InterpolableValue<Object, Boolean, InterpolableBoolean> {
  @InheritConstructors
  class RawValue extends InterpolableValue.RawValue<Object, Boolean, InterpolableBoolean, AlreadyInterpolated, Initialized, RawValue> implements InterpolableBoolean {
    @JsonCreator
    RawValue(Boolean rawValue) {
      super(rawValue)
    }

    @JsonCreator
    RawValue(SimpleInterpolableString rawValue) {
      super(rawValue)
    }

    protected static final Boolean doInterpolatePrimitive(Context context, Boolean rawValue) {
      rawValue
    }

    protected static final Boolean doInterpolatePrimitive(Context context, SimpleInterpolableString rawValue) {
      rawValue.interpolate context // TOTEST
    }
  }

  @InheritConstructors
  class Initialized extends InterpolableValue.Initialized<Object, Boolean, InterpolableBoolean, AlreadyInterpolated, Initialized> implements InterpolableBoolean { }

  @InheritConstructors
  class AlreadyInterpolated extends InterpolableValue.AlreadyInterpolated<Object, Boolean, InterpolableBoolean> implements InterpolableBoolean { }

  static final class Utils extends InterpolableValue.Utils {
    // This is used to create instances with default values
    static final InterpolableBoolean initWithDefault(InterpolableBoolean interpolableValue, Supplier<Boolean> defaultValueSupplier, Closure<Boolean> ignoreIf = null, Closure<Boolean> postProcess = null) {
      initWithDefault RawValue, interpolableValue, defaultValueSupplier, ignoreIf, postProcess
    }

    // This is used to create instances with default values
    static final InterpolableBoolean initWithDefault(InterpolableBoolean interpolableValue, Boolean defaultValue, Closure<Boolean> ignoreIf = null, Closure<Boolean> postProcess = null) {
      initWithDefault RawValue, interpolableValue, defaultValue, ignoreIf, postProcess
    }
  }
}
