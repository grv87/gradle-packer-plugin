package org.fidata.gradle.packer.tasks.arguments

import groovy.transform.CompileStatic

@CompileStatic
trait PackerTemplateArgument extends PackerTemplateReadOnlyArgument {
  void setTemplateFile(File templateFile) {
    this.templateFile = templateFile
  }
}
