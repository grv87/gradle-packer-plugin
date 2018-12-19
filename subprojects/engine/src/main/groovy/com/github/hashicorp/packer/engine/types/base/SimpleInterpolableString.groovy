package com.github.hashicorp.packer.engine.types.base

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonValue
import com.github.hashicorp.packer.template.Context
import groovy.transform.CompileStatic
import groovy.transform.Synchronized

// Note that this is not Serializable
// TOTHINK: Cloneable & empty constructor
@CompileStatic
class SimpleInterpolableString /*implements InterpolableObject<SimpleInterpolableString>*/ {
  private volatile String rawValue
  private volatile Object compiledTemplate = null

  @JsonCreator // (mode = JsonCreator.Mode.DELEGATING)
  SimpleInterpolableString(String rawValue) {
    setRawValue rawValue
  }

  @JsonValue
  String getRawValue() {
    this.@rawValue
  }

  @Synchronized
  void setRawValue(String rawValue) {
    this.@rawValue = rawValue
    compiledTemplate = Context.compileTemplate(rawValue)
  }

  // @Override
  String interpolate(Context context) {
    /*new SimpleInterpolableString(*/context.interpolateString(compiledTemplate)/*)*/ ?: null
  }
}
