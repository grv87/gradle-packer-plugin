package com.github.hashicorp.packer.engine.types

import com.github.hashicorp.packer.template.Context
import com.google.common.base.Supplier
import groovy.transform.AutoClone
import groovy.transform.AutoCloneStyle
import groovy.transform.CompileStatic
import com.fasterxml.jackson.annotation.JsonCreator
import groovy.transform.InheritConstructors

@CompileStatic
interface InterpolableBoolean extends InterpolableValue<Object, Boolean, InterpolableBoolean> {
  class Interpolable extends InterpolableValue.Interpolable<Object, Boolean, InterpolableBoolean, Interpolable, Initialized, AlreadyInterpolated> implements InterpolableBoolean {
    @JsonCreator
    Interpolable(Boolean rawValue) {
      super(rawValue)
    }

    @JsonCreator
    Interpolable(InterpolableString rawValue) {
      super(rawValue)
    }

    protected static final Boolean doInterpolatePrimitive(Context context, Boolean rawValue) {
      rawValue
    }

    protected static final Boolean doInterpolatePrimitive(Context context, InterpolableString rawValue) {
      rawValue.interpolate context // TOTEST
    }
  }

  @InheritConstructors
  class Initialized extends InterpolableValue.Initialized<Object, Boolean, InterpolableBoolean, Interpolable, Initialized, AlreadyInterpolated> implements InterpolableBoolean { }

  @InheritConstructors
  @AutoClone(style = AutoCloneStyle.SIMPLE)
  class AlreadyInterpolated extends InterpolableValue.AlreadyInterpolated<Object, Boolean, InterpolableBoolean, Interpolable, Initialized, AlreadyInterpolated> implements InterpolableBoolean { }

  static final class Utils extends InterpolableValue.Utils {
    // This is used to create instances with default values
    static final InterpolableBoolean initWithDefault(InterpolableBoolean interpolableValue, Supplier<Boolean> defaultValueSupplier, Closure<Boolean> ignoreIf = null, Closure<Boolean> postProcess = null) {
      initWithDefault Interpolable, interpolableValue, defaultValueSupplier, ignoreIf, postProcess
    }

    // This is used to create instances with default values
    static final InterpolableBoolean initWithDefault(InterpolableBoolean interpolableValue, Boolean defaultValue, Closure<Boolean> ignoreIf = null, Closure<Boolean> postProcess = null) {
      initWithDefault Interpolable, interpolableValue, defaultValue, ignoreIf, postProcess
    }
  }
}
