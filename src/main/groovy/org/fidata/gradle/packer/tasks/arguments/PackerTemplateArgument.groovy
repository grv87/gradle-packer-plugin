package org.fidata.gradle.packer.tasks.arguments

import groovy.transform.CompileStatic
import org.gradle.api.tasks.Internal

@CompileStatic
trait PackerTemplateArgument extends PackerArgument {
  private File templateFile
  @Internal
  File getTemplateFile() {
    this.templateFile
  }

  void setTemplateFile(File templateFile) {
    this.templateFile = templateFile
  }

  @Internal
  @Override
  List<Object> getCmdArgs() {
    List<Object> cmdArgs = (List<Object>)super.getCmdArgs()
    cmdArgs.add templateFile
    cmdArgs
  }
}
