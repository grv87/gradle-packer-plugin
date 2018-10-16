package org.fidata.gradle.packer.tasks.arguments

import groovy.transform.CompileStatic
import groovy.transform.SelfType
import org.gradle.api.tasks.Internal
import org.ysb33r.grolifant.api.exec.AbstractExecWrapperTask

@SelfType(AbstractExecWrapperTask)
@CompileStatic
trait PackerArgument {
  @Internal
  List<Object> getCmdArgs() {
    []
  }
}
