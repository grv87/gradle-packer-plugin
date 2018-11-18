package com.github.hashicorp.packer.engine.types

import com.fasterxml.jackson.annotation.JsonCreator
import com.github.hashicorp.packer.template.Context
import com.google.common.base.Supplier
import groovy.transform.AutoClone
import groovy.transform.AutoCloneStyle
import groovy.transform.CompileStatic
import groovy.transform.InheritConstructors
import groovy.transform.Synchronized

@CompileStatic
interface InterpolableString extends InterpolableValue<String, String, InterpolableString> {
  class Interpolable extends InterpolableValue.Interpolable<String, String, InterpolableString, Interpolable, Initialized, AlreadyInterpolated> implements InterpolableString {
    private volatile Object compiledTemplate = null

    @JsonCreator
    Interpolable(String rawValue) {
      super(rawValue)
      compileTemplate()
    }

    @Synchronized
    @Override
    void setRawValue(String rawValue) {
      super.setRawValue rawValue
      compileTemplate()
    }

    private void compileTemplate() {
      compiledTemplate = Context.compileTemplate(rawValue)
    }

    protected final String doInterpolatePrimitive(Context context, String rawValue) {
      context.interpolateString compiledTemplate
    }
  }

  @InheritConstructors
  class Initialized extends InterpolableValue.Initialized<String, String, InterpolableString, Interpolable, Initialized, AlreadyInterpolated> implements InterpolableString { }

  @InheritConstructors
  @AutoClone(style = AutoCloneStyle.SIMPLE)
  class AlreadyInterpolated extends InterpolableValue.AlreadyInterpolated<String, String, InterpolableString, Interpolable, Initialized, AlreadyInterpolated> implements InterpolableString { }

  static final class Utils extends InterpolableValue.Utils {
    // This is used to create instances with default values
    static final InterpolableString initWithDefault(InterpolableString interpolableValue, Supplier<String> defaultValueSupplier, Closure<Boolean> ignoreIf = null, Closure<String> postProcess = null) {
      initWithDefault Interpolable, interpolableValue, defaultValueSupplier, ignoreIf, postProcess
    }

    // This is used to create instances with default values
    static final InterpolableString initWithDefault(InterpolableString interpolableValue, String defaultValue, Closure<Boolean> ignoreIf = null, Closure<String> postProcess = null) {
      initWithDefault Interpolable, interpolableValue, defaultValue, ignoreIf, postProcess
    }
  }
}
