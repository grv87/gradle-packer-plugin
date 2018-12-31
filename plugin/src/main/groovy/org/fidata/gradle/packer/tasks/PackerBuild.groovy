package org.fidata.gradle.packer.tasks

import com.github.hashicorp.packer.template.Template
import groovy.transform.CompileStatic
import org.fidata.gradle.packer.tasks.arguments.PackerOnlyExceptArgument
import org.fidata.gradle.packer.tasks.arguments.PackerTemplateArgument

@CompileStatic
class PackerBuild extends AbstractPackerBuild implements PackerOnlyExceptArgument, PackerTemplateArgument {
  private Template template

  @Override
  Template getTemplate() {
    if (template?.path != templateFile.orNull?.asFile?.toPath()) {
      template = Template.readFromFile(engine, templateFile.get().asFile)
    }
    template
  }

  PackerBuild() {
    super(project.objects.fileProperty())
  }
}
