package org.fidata.gradle.packer.tasks.arguments

import groovy.transform.CompileStatic
import groovy.transform.SelfType
import org.fidata.gradle.packer.tasks.PackerWrapperTask
import org.gradle.api.tasks.Internal

@SelfType(PackerWrapperTask)
@CompileStatic
trait PackerArgument {
  @Internal
  List<Object> getCmdArgs() {
    []
  }
}
