package com.github.hashicorp.packer.engine.types

import groovy.transform.AutoClone
import groovy.transform.AutoCloneStyle
import groovy.transform.InheritConstructors
import groovy.transform.CompileStatic
import com.github.hashicorp.packer.engine.types.InterpolableValue

import java.nio.file.Path

@AutoClone(style = AutoCloneStyle.SIMPLE)
@InheritConstructors
@CompileStatic
class InterpolablePath extends InterpolableValue<InterpolableString, Path> {
  @Override
  protected Path doInterpolatePrimitive() {
    rawValue.interpolate context
    context.interpolatePath rawValue.interpolatedValue
  }
}
