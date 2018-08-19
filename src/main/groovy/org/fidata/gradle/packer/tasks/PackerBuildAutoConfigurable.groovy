package org.fidata.gradle.packer.tasks

import static org.gradle.language.base.plugins.LifecycleBasePlugin.BUILD_GROUP
import org.fidata.gradle.packer.template.Template
import javax.inject.Inject

class PackerBuildAutoConfigurable extends PackerBuild {
  @Override
  void setTemplateFile(File templateFile) {
    throw new UnsupportedOperationException('Setting templateFile property on PackerBuildAutoConfigurable task after its creation is not supported')
  }

  @Inject
  PackerBuildAutoConfigurable(File templateFile, Template template, Closure configureClosure = null) {
    super()
    this.org_fidata_gradle_packer_tasks_arguments_PackerTemplateArgument__templateFile = templateFile
    group = BUILD_GROUP
    this.template = template // TODO: It is not necesary to pass both template and templateFile
    configure configureClosure
    doConfigure() // TODO ?
  }

  protected void doConfigure() {
    // TODO
  }

}
