package com.github.hashicorp.packer.common

import org.fidata.gradle.utils.InputDirectoryWrapper
import org.fidata.packer.engine.annotations.AutoImplement
import org.fidata.packer.engine.annotations.ComputedInputFiles
import org.fidata.packer.engine.annotations.ComputedNested
import org.fidata.packer.engine.annotations.ExtraProcessed
import org.fidata.packer.engine.types.base.InterpolableObject
import org.fidata.packer.engine.types.InterpolableStringArray
import groovy.transform.CompileStatic

@AutoImplement
@CompileStatic
abstract class FloppyConfig implements InterpolableObject<FloppyConfig> {
  @ExtraProcessed
  abstract InterpolableStringArray getFloppyFiles()

  @ExtraProcessed
  abstract InterpolableStringArray getFloppyDirectories()

  // MARK2
  @ComputedNested
  @groovy.transform.Internal
  List<InputDirectoryWrapper> getInputDirectories() {

  }

  @ComputedNested
  @groovy.transform.Internal
  List<InputDirectoryWrapper> getInputDirectoriesFlat() {

  }

  @ComputedInputFiles
  @groovy.transform.Internal
  List<File> getInputFiles() {

  }

  // Wildcard characters (*, ?, and []) are allowed. Directory names are also allowed, which will add all the files found in the directory to the floppy
}
