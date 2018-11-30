package com.github.hashicorp.packer.engine.types

import com.fasterxml.jackson.annotation.JsonCreator
import com.github.hashicorp.packer.template.Context
import com.google.common.base.Supplier
import groovy.transform.CompileStatic
import groovy.transform.InheritConstructors
import com.fasterxml.jackson.databind.annotation.JsonDeserialize

@JsonDeserialize(as = RawValue)
@CompileStatic
interface InterpolableLong extends InterpolableValue<Object, Long, InterpolableLong> {
  @InheritConstructors
  final class RawValue extends InterpolableValue.RawValue<Object, Long, InterpolableLong, AlreadyInterpolated, Initialized> implements InterpolableLong {
    // This is required for initWithDefault
    RawValue() {
      super()
    }

    @JsonCreator
    RawValue(Long rawValue) {
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
      rawValue.interpolate(context).toLong()
    }
  }

  @InheritConstructors
  final class Initialized extends InterpolableValue.Initialized<Object, Long, InterpolableLong, AlreadyInterpolated, Initialized> implements InterpolableLong { }

  @InheritConstructors
  final class AlreadyInterpolated extends InterpolableValue.AlreadyInterpolated<Object, Long, InterpolableLong> implements InterpolableLong { }

  static final class Utils extends InterpolableValue.Utils {
    // This is used to create instances with default values
    static final InterpolableLong initWithDefault(InterpolableLong interpolableValue, Supplier<Long> defaultValueSupplier, Closure<Boolean> ignoreIf = null, Closure<Long> postProcess = null) {
      initWithDefault RawValue, interpolableValue, defaultValueSupplier, ignoreIf, postProcess
    }

    // This is used to create instances with default values
    static final InterpolableLong initWithDefault(InterpolableLong interpolableValue, Long defaultValue, Closure<Boolean> ignoreIf = null, Closure<Long> postProcess = null) {
      initWithDefault RawValue, interpolableValue, defaultValue, ignoreIf, postProcess
    }
  }
}
