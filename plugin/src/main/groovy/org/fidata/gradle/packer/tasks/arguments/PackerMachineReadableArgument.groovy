package org.fidata.gradle.packer.tasks.arguments

import groovy.transform.CompileStatic
import org.fidata.packer.engine.annotations.ExtraProcessed
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Console

@CompileStatic
trait PackerMachineReadableArgument extends PackerArgument {
  @Console
  // @Optional
  final Property<Boolean> machineReadable = project.objects.property(Boolean).convention(false)

  /*
   * WORKAROUND:
   * CodeNarc bug
   * Without getter we have error:
   * Call to super is not allowed in a trait
   * <grv87 2018-08-19>
   */
  @SuppressWarnings('UnnecessaryGetter')
  // TOTEST: @ExtraProcessed
  @Override
  List<String> getCmdArgs() {
    List<String> cmdArgs = super.getCmdArgs()
    if (machineReadable.getOrElse(false)) {
      cmdArgs.add '-machine-readable'
    }
    cmdArgs
  }
}
