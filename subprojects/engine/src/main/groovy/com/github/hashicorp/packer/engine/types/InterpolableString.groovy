package com.github.hashicorp.packer.engine.types

import groovy.transform.AutoClone
import groovy.transform.AutoCloneStyle
import groovy.transform.InheritConstructors
import groovy.transform.CompileStatic

@AutoClone(style = AutoCloneStyle.SIMPLE)
@InheritConstructors
@CompileStatic
class InterpolableString extends InterpolableValue<String, String> {
  protected final String doInterpolatePrimitive(String rawValue) {
    context.interpolateString rawValue
  }
}
