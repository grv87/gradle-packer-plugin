package com.github.hashicorp.packer.engine.types

import groovy.transform.AutoClone
import groovy.transform.AutoCloneStyle
import groovy.transform.InheritConstructors
import groovy.transform.CompileStatic

@AutoClone(style = AutoCloneStyle.SIMPLE)
@InheritConstructors
@CompileStatic
// @KnownImmutable // TODO: Groovy 2.5
class InterpolableString extends InterpolableValue<String, String> {
  @Override
  protected String doInterpolatePrimitive() {
    context.interpolateString(rawValue)
  }
}
