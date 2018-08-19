package org.fidata.gradle.packer.tasks.arguments

import groovy.transform.CompileStatic
import org.gradle.api.tasks.Console
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

  /*
   * WORKAROUND:
   * CodeNarc bug
   * Without getter we have error:
   * Call to super is not allowed in a trait
   * <grv87 2018-08-19>
   */
  @SuppressWarnings('UnnecessaryGetter')
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
