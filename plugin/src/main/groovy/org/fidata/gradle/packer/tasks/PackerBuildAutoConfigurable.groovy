package org.fidata.gradle.packer.tasks

import com.github.hashicorp.packer.template.OnlyExcept
import groovy.transform.CompileStatic
import groovy.transform.stc.ClosureParams
import groovy.transform.stc.FromString

import static org.gradle.language.base.plugins.LifecycleBasePlugin.BUILD_GROUP
import com.github.hashicorp.packer.template.Template
import javax.inject.Inject

@CompileStatic
class PackerBuildAutoConfigurable extends AbstractPackerBuild {
  private final Template template
  private Template templateForInterpolation

  @Override
  protected final Template getTemplateForInterpolation() {
    this.templateForInterpolation
  }

  @Override
  final Template getTemplate() {
    this.@template // TODO .clone()
  }

  @Inject
  PackerBuildAutoConfigurable(Template template, OnlyExcept onlyExcept, @ClosureParams(value = FromString, options = ['org.fidata.gradle.packer.tasks.PackerBuildAutoConfigurable', '']) @DelegatesTo(PackerBuildAutoConfigurable) Closure configureClosure) {
    super(project.layout.projectDirectory.file(project.provider { template.path.toString() }), onlyExcept)
    group = BUILD_GROUP
    this.template = template // TODO
    configure configureClosure // TODO: can be this escape
  }
}
