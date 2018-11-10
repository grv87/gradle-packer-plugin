/*
 * AbstractPackerBuild class
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

import static org.fidata.gradle.utils.StringUtils.stringize
import org.fidata.gradle.packer.tasks.arguments.PackerOnlyExceptReadOnlyArgument
import org.fidata.gradle.packer.tasks.arguments.PackerTemplateReadOnlyArgument
import org.gradle.api.provider.Provider
import com.github.hashicorp.packer.template.Builder
import com.github.hashicorp.packer.template.Context
import com.github.hashicorp.packer.engine.enums.OnError
import org.fidata.gradle.packer.tasks.arguments.PackerMachineReadableArgument
import org.gradle.api.logging.LogLevel
import org.gradle.api.tasks.Optional
import org.fidata.gradle.packer.PackerExecSpec
import org.fidata.gradle.packer.tasks.arguments.PackerVarArgument
import groovy.transform.CompileStatic
import com.github.hashicorp.packer.template.Template
import org.gradle.api.tasks.Internal
import org.gradle.api.tasks.Nested
import org.gradle.api.tasks.Console

@CompileStatic
abstract class AbstractPackerBuild extends PackerWrapperTask implements PackerMachineReadableArgument, PackerOnlyExceptReadOnlyArgument, PackerVarArgument, PackerTemplateReadOnlyArgument {
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
  @SuppressWarnings('UnnecessaryGetter') // TODO
  List<Object> getCmdArgs() {
    List<Object> cmdArgs = PackerTemplateReadOnlyArgument.super.getCmdArgs()
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
      cmdArgs.add 0, "-on-error=$onError"
    }
    if (project.gradle.startParameter.rerunTasks) {
      cmdArgs.add 0, '-force' // TODO: as property / Use Gradle rebuild CL argument
    }
    if ((project.logging.level ?: project.gradle.startParameter.logLevel) <= LogLevel.DEBUG) {
      cmdArgs.add 0, '-debug'
    }
    cmdArgs
  }

  @Internal
  abstract Template getTemplate()

  @Nested
  final Provider<List<Template>> interpolatedTemplates = project.providers.provider {
    if (!template.interpolated) { // TODO
      template.interpolate new Context(stringize(variables), stringize(environment), templateFile, workingDir.get().asFile.toPath())
    }

    List<Template> result = new ArrayList<>(onlyExcept.sizeAfterSkip(template.builders.size()))
    for (Builder builder in template.builders) {
      String buildName = builder.header.buildName
      if (!onlyExcept.skip(buildName)) {
        result.add template.interpolateForBuilder(buildName, project)
      }
    }
    result
  }

  AbstractPackerBuild() {
    outputs.upToDateWhen {
      interpolatedTemplates.get().every() { Template interpolatedTemplate ->
        !interpolatedTemplate.upToDateWhen || interpolatedTemplate.upToDateWhen.every { Provider<Boolean> upToDateWhenProvider ->
          upToDateWhenProvider.get()
        }
      }
    }
  }

  @Override
  protected PackerExecSpec configureExecSpec(PackerExecSpec execSpec) {
    PackerExecSpec result = super.configureExecSpec(execSpec)
    result.command 'build'
    result
  }
}
