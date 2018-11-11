package com.github.hashicorp.packer.engine.types

import groovy.transform.AutoClone
import groovy.transform.AutoCloneStyle
import groovy.transform.InheritConstructors
import groovy.transform.CompileStatic

import java.util.concurrent.Callable

@AutoClone(style = AutoCloneStyle.SIMPLE)
@InheritConstructors
@CompileStatic
// Callable is required for InputFile etc. annotations
class InterpolableFile extends InterpolableValue<InterpolableString, File> implements Callable<File> {
  protected final File doInterpolatePrimitive(InterpolableString rawValue) {
    context.interpolatePath(rawValue.interpolatedValue(context)).toFile()
  }

  @Override
  File call() {
    interpolatedValue
  }

  // TODO: default ?
}
