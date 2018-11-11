/*
 * PackerValidate class
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

import groovy.transform.CompileStatic
import org.fidata.gradle.packer.PackerExecSpec
import org.fidata.gradle.packer.tasks.arguments.PackerOnlyExceptArgument
import org.fidata.gradle.packer.tasks.arguments.PackerTemplateArgument
import org.fidata.gradle.packer.tasks.arguments.PackerVarArgument
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.InputFile
import org.gradle.api.tasks.Internal
import org.gradle.api.tasks.Optional

@CompileStatic
class PackerValidate extends PackerWrapperTask implements PackerOnlyExceptArgument, PackerVarArgument, PackerTemplateArgument {
  @Input
  @Optional
  Property<Boolean> syntaxOnly = project.objects.property(Boolean)

  @Internal
  @Override
  @SuppressWarnings('UnnecessaryGetter') // TODO
  List<Object> getCmdArgs() {
    List<Object> cmdArgs = PackerTemplateArgument.super.getCmdArgs()
    if (syntaxOnly.getOrElse(false)) {
      cmdArgs.add 0, '-syntax-only' // Template should be the last, so we insert in the start
    }
    cmdArgs
  }

  @InputFile
  @Override
  RegularFileProperty getTemplateFile() {
    PackerTemplateArgument.super.templateFile
  }

  PackerValidate() {
    syntaxOnly.set false
    this.@org_fidata_gradle_packer_tasks_arguments_PackerTemplateReadOnlyArgument__templateFile = newInputFile() // TOTEST: it should be still internal
    outputs.upToDateWhen { true } // TODO ? Is it standard for code quality tasks ?
  }

  @Override
  protected PackerExecSpec configureExecSpec(PackerExecSpec execSpec) {
    PackerExecSpec result = super.configureExecSpec(execSpec)
    result.command 'validate'
    result
  }
}
