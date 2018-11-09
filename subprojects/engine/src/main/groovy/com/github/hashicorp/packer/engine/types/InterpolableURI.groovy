package com.github.hashicorp.packer.engine.types

import com.fasterxml.jackson.annotation.JsonIgnore
import groovy.transform.AutoClone
import groovy.transform.AutoCloneStyle
import groovy.transform.CompileStatic
import groovy.transform.InheritConstructors
import org.gradle.api.tasks.Internal

@AutoClone(style = AutoCloneStyle.SIMPLE)
@InheritConstructors
@CompileStatic
class InterpolableURI extends InterpolableValue<InterpolableString, URI> {
  @JsonIgnore
  @Internal
  URI getFileURI() {
    interpolatedValue?.scheme == 'file' ? interpolatedValue : null
  }

  @JsonIgnore
  @Internal
  URI getNonFileURI() {
    interpolatedValue?.scheme != 'file' ? interpolatedValue : null
  }

  @Override
  protected final URI doInterpolatePrimitive() {
    rawValue.interpolate context
    context.resolveUri rawValue.interpolatedValue
  }
}
