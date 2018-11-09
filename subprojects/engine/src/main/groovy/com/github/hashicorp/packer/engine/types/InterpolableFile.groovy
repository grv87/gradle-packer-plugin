package com.github.hashicorp.packer.engine.types

import groovy.transform.AutoClone
import groovy.transform.AutoCloneStyle
import groovy.transform.InheritConstructors
import groovy.transform.CompileStatic

@AutoClone(style = AutoCloneStyle.SIMPLE)
@InheritConstructors
@CompileStatic
class InterpolableFile extends InterpolableValue<InterpolableString, File> {
  @Override
  protected final File doInterpolatePrimitive() {
    rawValue.interpolate context
    context.interpolatePath(rawValue.interpolatedValue).toFile()
  }
}
