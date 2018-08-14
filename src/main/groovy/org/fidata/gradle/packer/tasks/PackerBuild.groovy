/*
 * PackerBuild class
 * Copyright © 2018  Basil Peace
 *
 * This file is part of gradle-packer-plugin.
 *
 * This plugin is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License
 * as published by the Free Software Foundation, either version 3
 * of the License, or (at your option) any later version.
 *
 * This plugin is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this plugin.  If not, see <https://www.gnu.org/licenses/>.
 */
package org.fidata.gradle.packer.tasks

import org.fidata.gradle.packer.tasks.arguments.PackerMachineReadableArgument
import org.gradle.api.logging.LogLevel

import static org.gradle.language.base.plugins.LifecycleBasePlugin.BUILD_GROUP
import org.fidata.gradle.packer.PackerExecSpec
import org.fidata.gradle.packer.tasks.arguments.PackerOnlyExceptArgument
import org.fidata.gradle.packer.tasks.arguments.PackerTemplateArgument
import org.fidata.gradle.packer.tasks.arguments.PackerVarArgument
import groovy.transform.CompileStatic
import org.fidata.gradle.packer.template.Context
import org.fidata.gradle.packer.template.Template
import org.gradle.api.provider.Provider
import org.gradle.api.tasks.Internal
import org.gradle.api.tasks.Nested
import javax.inject.Inject

@CompileStatic
class PackerBuild extends PackerWrapperTask implements PackerMachineReadableArgument, PackerOnlyExceptArgument, PackerVarArgument, PackerTemplateArgument {
  @Internal
  @Override
  List<Object> getCmdArgs() {
    List<Object> cmdArgs = PackerTemplateArgument.super.getCmdArgs()
    if ((project.logging.level ?: project.gradle.startParameter.logLevel) <= LogLevel.DEBUG) {
      cmdArgs.add 0, '-debug' // Template should be the last, so we insert in the start
    }
    cmdArgs
  }

  @Override
  void setTemplateFile(File templateFile) {
    throw new UnsupportedOperationException('Setting templateFile property on PackerBuild task after its creation is not supported')
  }

  private Template template

  @Internal
  Template getTemplate() {
    this.template
  }

  @Nested
  Provider<List<Template>> getInterpolatedTemplates() {
    Context ctx = new Context()
    /*for (Builder builder in template.builders) {
      if (onlyExcept.skip(builder.header.name))
    }
    template.clone()*/
    null
  }

  @Inject
  PackerBuild(File templateFile, Template template, Closure configureClosure = null) {
    super()
    // this.org_fidata_gradle_packer_tasks_arguments_PackerTemplateArgument__templateFile = templateFile
    group = BUILD_GROUP
    // this.template = template // TODO: It is not necesary to pass both template and templateFile
    configure configureClosure
    doConfigure() // TODO ?
  }

  protected void doConfigure() {
    // TODO
  }

  @Override
  protected PackerExecSpec configureExecSpec(PackerExecSpec execSpec) {
    super.configureExecSpec(execSpec)
    execSpec.command 'build'
    execSpec
  }
}
