package org.fidata.gradle.packer.tasks.arguments

import groovy.transform.CompileStatic
import org.gradle.api.file.RegularFile
import org.gradle.api.provider.Provider
import org.gradle.api.tasks.Internal

@CompileStatic
trait PackerTemplateReadOnlyArgument extends PackerArgument {
  @Internal
  final Provider<RegularFile> templateFile

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
    cmdArgs.add templateFile
    cmdArgs
  }
}
