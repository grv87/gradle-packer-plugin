package com.github.hashicorp.packer.engine.types

import com.github.hashicorp.packer.engine.annotations.ComputedInputDirectory
import groovy.transform.CompileStatic
import groovy.transform.InheritConstructors
import java.nio.file.Path

@InheritConstructors
@CompileStatic
// This class is required to overcome the fact that Gradle doesn't have InputDirectories annotation
class InterpolableInputDirectory extends InterpolableFile {
  @ComputedInputDirectory
  File getInterpolatedValue() { // TODO: Directory ?
    super.interpolatedValue
  }
}