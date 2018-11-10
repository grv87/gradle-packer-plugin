package com.github.hashicorp.packer.engine.types

import com.fasterxml.jackson.annotation.JsonIgnore
import groovy.transform.AutoClone
import groovy.transform.AutoCloneStyle
import groovy.transform.CompileStatic
import groovy.transform.InheritConstructors
import org.gradle.api.tasks.InputDirectory
import java.nio.file.Path

@AutoClone(style = AutoCloneStyle.SIMPLE)
@InheritConstructors
@CompileStatic
// This class is required to overcome the fact that Gradle doesn't have InputDirectories annotation
class InterpolableInputDirectory extends InterpolableFile {
  @JsonIgnore
  @InputDirectory
  File getInterpolatedValue() { // TODO: Directory ?
    super.interpolatedValue
  }
}