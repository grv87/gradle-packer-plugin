package com.github.hashicorp.packer.engine.types

import com.github.hashicorp.packer.engine.annotations.ComputedInternal
import groovy.transform.AutoClone
import groovy.transform.AutoCloneStyle
import groovy.transform.CompileStatic
import groovy.transform.InheritConstructors

@AutoClone(style = AutoCloneStyle.SIMPLE)
@InheritConstructors
@CompileStatic
class InterpolableURI extends InterpolableValue<InterpolableString, URI> {
  @ComputedInternal
  URI getFileURI() {
    interpolatedValue?.scheme == 'file' ? interpolatedValue : null
  }

  @ComputedInternal
  URI getNonFileURI() {
    interpolatedValue?.scheme != 'file' ? interpolatedValue : null
  }

  protected final URI doInterpolatePrimitive(InterpolableString rawValue) {
    context.resolveUri rawValue.interpolatedValue(context)
  }
}
