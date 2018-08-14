package org.fidata.gradle.packer.tasks.arguments

import groovy.transform.CompileStatic
import org.fidata.gradle.packer.template.OnlyExcept
import org.gradle.api.tasks.Console
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.Internal
import org.gradle.api.tasks.Optional

@CompileStatic
trait PackerMachineReadableArgument extends PackerArgument {
  private Boolean machineReadable = false

  @Console
  @Optional
  Boolean getMachineReadable() {
    this.machineReadable
  }

  void setMachineReadable(Boolean machineReadable) {
    this.machineReadable = machineReadable
  }

  @Internal
  @Override
  List<Object> getCmdArgs() {
    List<Object> cmdArgs = (List<Object>)super.getCmdArgs()
    if (machineReadable) {
      cmdArgs.add '-machine-readable'
    }
    cmdArgs
  }
}
