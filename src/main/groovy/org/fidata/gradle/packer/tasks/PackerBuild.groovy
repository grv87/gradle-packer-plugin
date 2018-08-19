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

import org.fidata.gradle.packer.template.Builder

import static org.gradle.language.base.plugins.LifecycleBasePlugin.BUILD_GROUP
import org.fidata.gradle.packer.enums.OnError
import org.fidata.gradle.packer.tasks.arguments.PackerMachineReadableArgument
import org.gradle.api.logging.LogLevel
import org.gradle.api.tasks.Optional
import org.fidata.gradle.packer.PackerExecSpec
import org.fidata.gradle.packer.tasks.arguments.PackerOnlyExceptArgument
import org.fidata.gradle.packer.tasks.arguments.PackerTemplateArgument
import org.fidata.gradle.packer.tasks.arguments.PackerVarArgument
import groovy.transform.CompileStatic
import org.fidata.gradle.packer.template.Template
import org.gradle.api.tasks.Internal
import org.gradle.api.tasks.Nested
import org.gradle.api.tasks.Console

@CompileStatic
class PackerBuild extends PackerWrapperTask implements PackerMachineReadableArgument, PackerOnlyExceptArgument, PackerVarArgument, PackerTemplateArgument {
  @Console
  @Optional
  Boolean color = true

  @Internal
  @Optional
  Boolean parallel = true

  @Internal
  @Optional
  OnError onError = OnError.CLEANUP

  @Internal
  @Override
  List<Object> getCmdArgs() {
    List<Object> cmdArgs = PackerTemplateArgument.super.getCmdArgs()
    // Template should be the last, so we insert in the start
    if (!color) {
      cmdArgs.add 0, '-color=false'
    }
    if (!parallel) {
      cmdArgs.add 0, '-parallel=false'
    }
    if (onError) {
      /*if (onError == OnError.ASK &&  {
        TODO: ASK will work in interactive mode only
      }*/
      cmdArgs.add 0, "-on-error=${ onError.name().toLowerCase() }"
    }
    if(project.gradle.startParameter.rerunTasks) {
      cmdArgs.add 0, '-force' // TODO: as property
    }
    if ((project.logging.level ?: project.gradle.startParameter.logLevel) <= LogLevel.DEBUG) {
      cmdArgs.add 0, '-debug'
    }
    cmdArgs
  }

  private Template template

  @Internal
  Template getTemplate() {
    this.template
  }

  @Nested
  List<Template> getInterpolatedTemplates() {
    List<Template> result = new ArrayList<>(onlyExcept.sizeAfterSkip(template.builders.size()))
    for (Builder builder in template.builders) {
      String buildName = builder.header.buildName
      if (!onlyExcept.skip(buildName)) {
        result.add template.interpolateBuilder(buildName)
      }
    }
    result
  }

  @Override
    protected PackerExecSpec configureExecSpec(PackerExecSpec execSpec) {
    execSpec = super.configureExecSpec(execSpec)
    execSpec.command 'build'
    execSpec
  }
}
