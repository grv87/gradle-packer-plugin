package com.github.hashicorp.packer.engine.types

import com.fasterxml.jackson.annotation.JsonIgnore
import groovy.transform.AutoClone
import groovy.transform.AutoCloneStyle
import groovy.transform.CompileStatic
import groovy.transform.InheritConstructors
import org.gradle.api.tasks.InputFile
import java.nio.file.Path

@AutoClone(style = AutoCloneStyle.SIMPLE)
@InheritConstructors
@CompileStatic
class InterpolableInputRegularFile extends InterpolableFile {
  @JsonIgnore
  @InputFile
  File getInterpolatedValue() { // TODO: RegularFile ?
    super.interpolatedValue
  }
}
