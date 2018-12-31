package org.fidata.gradle.packer.tasks

import com.github.hashicorp.packer.template.OnlyExcept
import groovy.transform.CompileStatic
import com.github.hashicorp.packer.template.Template
import javax.inject.Inject

@CompileStatic
class PackerBuildAutoConfigurable extends AbstractPackerBuild {
  private final Template template

  @Override
  final Template getTemplate() {
    this.@template
  }

  @Inject
  PackerBuildAutoConfigurable(Template template, OnlyExcept onlyExcept) {
    super(project.layout.file(project.provider { template.path.toFile() }), onlyExcept)
    this.@template = template
  }
}
