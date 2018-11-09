package com.github.hashicorp.packer.engine.types

import com.fasterxml.jackson.annotation.JsonIgnore
import groovy.transform.AutoClone
import groovy.transform.AutoCloneStyle
import groovy.transform.CompileStatic
import groovy.transform.InheritConstructors
import org.gradle.api.tasks.InputFile
import org.gradle.api.tasks.Internal
import org.gradle.api.tasks.Optional

@AutoClone(style = AutoCloneStyle.SIMPLE)
@InheritConstructors
@CompileStatic
class InterpolableInputURI extends InterpolableURI {
  @JsonIgnore
  @InputFile
  @Optional
  URI getFileURI() { // TODO: RegularFile ?
    super.fileURI
  }

  @JsonIgnore
  @Internal
  @Optional
  URI getNonFileURI() {
    super.nonFileURI
  }
}
