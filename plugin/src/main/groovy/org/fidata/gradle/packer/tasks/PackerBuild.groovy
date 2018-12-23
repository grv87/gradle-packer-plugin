package org.fidata.gradle.packer.tasks

import com.github.hashicorp.packer.engine.Engine
import com.github.hashicorp.packer.template.Template
import groovy.transform.CompileStatic
import org.fidata.gradle.packer.PackerBasePlugin
import org.fidata.gradle.packer.tasks.arguments.PackerOnlyExceptArgument
import org.fidata.gradle.packer.tasks.arguments.PackerTemplateArgument

@CompileStatic
class PackerBuild extends AbstractPackerBuild implements PackerOnlyExceptArgument, PackerTemplateArgument {
  private static final Engine ENGINE = new Engine()
  
  private Template template

  // returns null if templateFile is empty
  @Override
  Template getTemplateForInterpolation() {
    if (template?.path != templateFile.orNull?.asFile?.toPath()) {
      template = Template.readFromFile(project.getPlugin(PackerBasePlugin).engine, templateFile.get().asFile)
    }
    template
  }

  // returns null if templateFile is empty
  @Override
  Template getTemplate() {
    templateForInterpolation.clone() // TODO
  }

  PackerBuild() {
    super(newInputFile()) // TOTEST: it should be still internal
  }
}
