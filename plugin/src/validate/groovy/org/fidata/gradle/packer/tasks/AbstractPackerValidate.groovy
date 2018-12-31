/*
 * AbstractPackerValidate class
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

import com.github.hashicorp.packer.template.OnlyExcept
import groovy.transform.CompileStatic
import org.fidata.gradle.packer.PackerExecSpec
import org.fidata.gradle.packer.tasks.arguments.PackerOnlyExceptReadOnlyArgument
import org.fidata.gradle.packer.tasks.arguments.PackerTemplateReadOnlyArgument
import org.fidata.gradle.packer.tasks.arguments.PackerVarArgument
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.file.RegularFile
import org.gradle.api.provider.MapProperty
import org.gradle.api.provider.Property
import org.gradle.api.provider.Provider
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.InputFile
import org.gradle.api.tasks.Internal
import org.gradle.api.tasks.Optional

@CompileStatic
abstract class AbstractPackerValidate extends PackerWrapperTask implements PackerOnlyExceptReadOnlyArgument, PackerVarArgument, PackerTemplateReadOnlyArgument {
  @Input
  @Optional
  @Override
  MapProperty<String, String> getEnv() {
    super.env
  }

  @Input
  @Override
  DirectoryProperty getWorkingDir() {
    super.workingDir
  }

  @Input
  @Optional
  @Override
  MapProperty<String, String> getVariables() {
    PackerVarArgument.super.variables
  }

  @InputFile
  @Override
  Provider<RegularFile> getTemplateFile() {
    PackerTemplateReadOnlyArgument.super.templateFile
  }

  @Input
  @Optional
  @Override
  OnlyExcept getOnlyExcept() {
    PackerOnlyExceptReadOnlyArgument.super.onlyExcept
  }

  @Input
  @Optional
  final Property<Boolean> syntaxOnly

  AbstractPackerValidate(Provider<RegularFile> templateFile, OnlyExcept onlyExcept = null) {
    PackerTemplateReadOnlyArgument.super.templateFile = templateFile

    PackerOnlyExceptReadOnlyArgument.super.onlyExcept = onlyExcept

    syntaxOnly = project.objects.property(Boolean)
    syntaxOnly.convention Boolean.FALSE

    outputs.upToDateWhen { true } // TODO ? Is it standard for code quality tasks ?
  }

  // TOTEST: @Internal
  @Override
  @SuppressWarnings('UnnecessaryGetter') // TODO
  List<String> getCmdArgs() {
    List<String> cmdArgs = PackerTemplateReadOnlyArgument.super.getCmdArgs()
    if (syntaxOnly.getOrElse(false)) {
      cmdArgs.add 0, '-syntax-only' // Template should be the last, so we insert in the start
    }
    cmdArgs
  }

  @Override
  protected PackerExecSpec configureExecSpec(PackerExecSpec execSpec) {
    PackerExecSpec result = super.configureExecSpec(execSpec)
    result.command 'validate'
    result
  }
}
