package org.fidata.gradle.packer.tasks.arguments

import groovy.transform.CompileStatic
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.tasks.Internal

@CompileStatic
trait PackerTemplateArgument extends PackerTemplateReadOnlyArgument {
  // TOTEST @Internal
  @Override
  RegularFileProperty getTemplateFile() {
    (RegularFileProperty)super.@templateFile
  }
}
