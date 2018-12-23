package org.fidata.gradle.packer.tasks.arguments

import groovy.transform.CompileStatic
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Console
import org.gradle.api.tasks.Internal
import org.gradle.api.tasks.Optional

@CompileStatic
trait PackerMachineReadableArgument extends PackerArgument {
  @Console
  @Optional
  final Property<Boolean> machineReadable = project.objects.property(Boolean)

  /*TOTEST
  @Console
  @Optional
  Boolean getMachineReadable() {
    this.machineReadable
  }

  void setMachineReadable(Boolean machineReadable) {
    this.machineReadable = machineReadable
  }*/

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
    if (machineReadable.getOrElse(false)) {
      cmdArgs.add '-machine-readable'
    }
    cmdArgs
  }

  // TODO (make custom chained method and call from instance constructor ?)
  PackerMachineReadableArgument() {
    machineReadable.set false
  }
}
