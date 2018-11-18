package com.github.hashicorp.packer.engine.types

import com.fasterxml.jackson.annotation.JsonCreator
import com.github.hashicorp.packer.template.Context
import com.google.common.base.Supplier
import groovy.transform.AutoClone
import groovy.transform.AutoCloneStyle
import groovy.transform.CompileStatic
import groovy.transform.InheritConstructors

@CompileStatic
interface InterpolableLong extends InterpolableValue<Object, Long, InterpolableLong> {
  class Interpolable extends InterpolableValue.Interpolable<Object, Long, InterpolableLong, Interpolable, Initialized, AlreadyInterpolated> implements InterpolableLong {
    @JsonCreator
    Interpolable(Long rawValue) {
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
      ((InterpolableString)rawValue.apply(context)).get().toLong() // TODO
    }
  }

  @InheritConstructors
  class Initialized extends InterpolableValue.Initialized<Object, Long, InterpolableLong, Interpolable, Initialized, AlreadyInterpolated> implements InterpolableLong { }

  @InheritConstructors
  @AutoClone(style = AutoCloneStyle.SIMPLE)
  class AlreadyInterpolated extends InterpolableValue.AlreadyInterpolated<Object, Long, InterpolableLong, Interpolable, Initialized, AlreadyInterpolated> implements InterpolableLong { }

  static final class Utils extends InterpolableValue.Utils {
    // This is used to create instances with default values
    static final InterpolableLong initWithDefault(InterpolableLong interpolableValue, Supplier<Long> defaultValueSupplier, Closure<Boolean> ignoreIf = null, Closure<Long> postProcess = null) {
      initWithDefault Interpolable, interpolableValue, defaultValueSupplier, ignoreIf, postProcess
    }

    // This is used to create instances with default values
    static final InterpolableLong initWithDefault(InterpolableLong interpolableValue, Long defaultValue, Closure<Boolean> ignoreIf = null, Closure<Long> postProcess = null) {
      initWithDefault Interpolable, interpolableValue, defaultValue, ignoreIf, postProcess
    }
  }
}
