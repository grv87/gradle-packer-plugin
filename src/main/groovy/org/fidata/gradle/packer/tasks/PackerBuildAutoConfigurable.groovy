package org.fidata.gradle.packer.tasks

import com.github.hashicorp.packer.template.OnlyExcept

import static org.gradle.language.base.plugins.LifecycleBasePlugin.BUILD_GROUP
import com.github.hashicorp.packer.template.Template
import javax.inject.Inject

class PackerBuildAutoConfigurable extends AbstractPackerBuild {
  private final Template template

  @Override
  final Template getTemplate() {
    this.template
  }

  @Inject
  PackerBuildAutoConfigurable(File templateFile, Template template, OnlyExcept onlyExcept, @DelegatesTo(PackerBuildAutoConfigurable) Closure configureClosure = null) {
    super()
    group = BUILD_GROUP
    this.org_fidata_gradle_packer_tasks_arguments_PackerTemplateArgument__templateFile = templateFile // TOTEST: without trait
    this.template = template // TODO: It is not necessary to pass both template and templateFile. However, it is cheaper to clone existing template instance than parse again
    this.org_fidata_gradle_packer_tasks_arguments_PackerOnlyExceptArgument__onlyExcept = onlyExcept // TOTEST: without trait
    configure configureClosure
  }
}
