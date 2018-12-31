package org.fidata.gradle.packer.tasks.arguments

import groovy.transform.CompileStatic
import groovy.transform.SelfType
import org.fidata.gradle.packer.tasks.PackerWrapperTask
import org.fidata.packer.engine.annotations.ExtraProcessed

@SelfType(PackerWrapperTask)
@CompileStatic
trait PackerArgument {
  @ExtraProcessed // TOTHINK
  List<String> getCmdArgs() {
    []
  }
}
