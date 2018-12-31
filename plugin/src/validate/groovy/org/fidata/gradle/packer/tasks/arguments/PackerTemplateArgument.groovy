package org.fidata.gradle.packer.tasks.arguments

import groovy.transform.CompileStatic
import org.gradle.api.file.RegularFileProperty

@CompileStatic
trait PackerTemplateArgument extends PackerTemplateReadOnlyArgument {
  // TOTEST @Internal
  @Override
  RegularFileProperty getTemplateFile() {
    (RegularFileProperty)super.templateFile
  }
}
