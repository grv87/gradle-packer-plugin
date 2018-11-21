package com.github.hashicorp.packer.engine.types

import com.github.hashicorp.packer.template.Context
import com.google.common.base.Supplier
import groovy.transform.CompileStatic
import com.fasterxml.jackson.annotation.JsonCreator
import groovy.transform.InheritConstructors
import com.fasterxml.jackson.databind.annotation.JsonDeserialize

@JsonDeserialize(as = ReadOnlyRawValue)
@CompileStatic
interface InterpolableBoolean extends InterpolableValue<Object, Boolean, InterpolableBoolean> {
  @InheritConstructors
  class ReadOnlyRawValue extends InterpolableValue.ReadOnlyRawValue<Object, Boolean, InterpolableBoolean, ReadOnlyRawValue, ReadWriteRawValue, ReadOnlyInitialized, ReadWriteInitialized, AlreadyInterpolated> implements InterpolableBoolean {
    @JsonCreator
    ReadOnlyRawValue(Boolean rawValue) {
      super(rawValue)
    }

    @JsonCreator
    ReadOnlyRawValue(SimpleInterpolableString rawValue) {
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
  class ReadWriteRawValue extends InterpolableValue.ReadWriteRawValue<Object, Boolean, InterpolableBoolean, ReadOnlyRawValue, ReadWriteRawValue, ReadOnlyInitialized, ReadWriteInitialized, AlreadyInterpolated> implements InterpolableBoolean {
    @JsonCreator
    ReadWriteRawValue(Boolean rawValue) {
      super(rawValue)
    }

    @JsonCreator
    ReadWriteRawValue(SimpleInterpolableString rawValue) {
      super(rawValue)
    }

    void setRawValue(Boolean rawValue) {
      super.rawValue = rawValue
    }

    void setRawValue(SimpleInterpolableString rawValue) {
      super.rawValue = rawValue
    }

    protected static final Boolean doInterpolatePrimitive(Context context, Boolean rawValue) {
      rawValue
    }

    protected static final Boolean doInterpolatePrimitive(Context context, SimpleInterpolableString rawValue) {
      rawValue.interpolate context // TOTEST
    }
  }

  @InheritConstructors
  class ReadOnlyInitialized extends InterpolableValue.ReadOnlyInitialized<Object, Boolean, InterpolableBoolean, ReadOnlyRawValue,ReadWriteRawValue, ReadOnlyInitialized, ReadWriteInitialized, AlreadyInterpolated> implements InterpolableBoolean { }

  @InheritConstructors
  class ReadWriteInitialized extends InterpolableValue.ReadWriteInitialized<Object, Boolean, InterpolableBoolean, ReadOnlyRawValue,ReadWriteRawValue, ReadOnlyInitialized, ReadWriteInitialized, AlreadyInterpolated> implements InterpolableBoolean { }

  @InheritConstructors
  class AlreadyInterpolated extends InterpolableValue.AlreadyInterpolated<Object, Boolean, InterpolableBoolean, ReadOnlyRawValue,ReadWriteRawValue, ReadOnlyInitialized, ReadWriteInitialized, AlreadyInterpolated> implements InterpolableBoolean { }

  static final class Utils extends InterpolableValue.Utils {
    // This is used to create instances with default values
    static final InterpolableBoolean initWithDefault(boolean readOnly, InterpolableBoolean interpolableValue, Supplier<Boolean> defaultValueSupplier, Closure<Boolean> ignoreIf = null, Closure<Boolean> postProcess = null) {
      initWithDefault readOnly, /*TODO: InterpolableValue.AbstractRawValue<Object, Boolean, InterpolableBoolean, ReadOnlyRawValue, ReadWriteRawValue, ReadOnlyInitialized, ReadWriteInitialized, AlreadyInterpolated>.class,*/ ReadOnlyRawValue, ReadWriteRawValue, interpolableValue, defaultValueSupplier, ignoreIf, postProcess
    }

    // This is used to create instances with default values
    static final InterpolableBoolean initWithDefault(boolean readOnly, InterpolableBoolean interpolableValue, Boolean defaultValue, Closure<Boolean> ignoreIf = null, Closure<Boolean> postProcess = null) {
      initWithDefault readOnly, ReadOnlyRawValue, ReadWriteRawValue, interpolableValue, defaultValue, ignoreIf, postProcess
    }
  }
}
