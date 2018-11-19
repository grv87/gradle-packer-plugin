package com.github.hashicorp.packer.engine.types

import com.github.hashicorp.packer.engine.annotations.ComputedInputFile
import com.github.hashicorp.packer.engine.annotations.ComputedInternal
import groovy.transform.CompileStatic
import groovy.transform.InheritConstructors
import org.gradle.api.tasks.Optional

@InheritConstructors
@CompileStatic
class InterpolableInputURI extends InterpolableURI {
  @ComputedInputFile
  @Optional
  URI getFileURI() { // TODO: RegularFile ?
    super.fileURI
  }

  @ComputedInternal
  @Optional
  URI getNonFileURI() {
    super.nonFileURI
  }
}
