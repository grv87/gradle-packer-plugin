package org.fidata.gradle.packer.tasks

import com.github.hashicorp.packer.template.OnlyExcept
import groovy.transform.CompileStatic
import static org.gradle.language.base.plugins.LifecycleBasePlugin.BUILD_GROUP
import com.github.hashicorp.packer.template.Template
import javax.inject.Inject

@CompileStatic
class PackerBuildAutoConfigurable extends AbstractPackerBuild {
  private final Template template

  @Override
  Template getTemplate() {
    this.template
  }

  @Inject
  PackerBuildAutoConfigurable(Template template, OnlyExcept onlyExcept, @DelegatesTo(PackerBuildAutoConfigurable) Closure configureClosure) {
    super(project.layout.projectDirectory.file(project.provider { template.path.toString() }), onlyExcept)
    group = BUILD_GROUP
    this.template = template // TODO
    configure configureClosure
  }
}
