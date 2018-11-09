/*
 * File class
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
package com.github.hashicorp.packer.provisioner

import com.fasterxml.jackson.annotation.JsonIgnore
import groovy.transform.AutoClone
import groovy.transform.AutoCloneStyle
import groovy.transform.CompileStatic
import com.github.hashicorp.packer.template.Provisioner
import com.github.hashicorp.packer.engine.annotations.Default
import com.github.hashicorp.packer.engine.types.InterpolableObject
import com.github.hashicorp.packer.engine.types.InterpolableBoolean
import com.github.hashicorp.packer.engine.types.InterpolablePath
import com.github.hashicorp.packer.engine.types.InterpolableString
import com.github.hashicorp.packer.engine.types.InterpolableStringArray
import org.gradle.api.file.ConfigurableFileTree
import org.gradle.api.file.FileTree
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.InputFiles
import org.gradle.api.tasks.Internal
import org.gradle.api.tasks.Nested
import org.gradle.api.tasks.Optional

@AutoClone(style = AutoCloneStyle.SIMPLE)
@CompileStatic
class Converge extends Provisioner<Configuration> {
  Converge() {
    super(Configuration)
  }

  static class Configuration extends Provisioner.Configuration {
    @Default(value = 'true')
    @Input
    InterpolableBoolean bootstrap

    @Input
    InterpolableString version

    @Input
    InterpolableString bootstrapCommand

    @Input
    InterpolableBoolean preventBootstrapSudo

    @Nested
    List<ModuleDir> moduleDirs

    @Input
    InterpolableString module

    @Input
    InterpolableString workingDirectory

    @Input
    Map<String, InterpolableString> params

    @Input
    InterpolableString executeCommand

    @Input
    InterpolableBoolean preventSudo

    static class ModuleDir extends InterpolableObject {
      @Internal
      InterpolablePath source

      @Input
      InterpolableString destination

      @Internal
      InterpolableStringArray exclude

      // TODO

      @JsonIgnore
      @InputFiles
      FileTree inputFileTree

      @Override
      protected void doInterpolate() {
        source.interpolate context
        destination.interpolate context
        exclude.interpolate context
        if (exclude?.interpolatedValue?.size() > 0) {
          inputFileTree = context.resolveFileTree(source.interpolatedValue) { ConfigurableFileTree configurableFileTree ->
            configurableFileTree.exclude exclude.interpolatedValue
          }
        } else {
          /*
           * CAVEAT:
           * We make shortcut and save input directory as file tree, to save some code.
           * There could be some troubles with this approach
           */
          inputFileTree = context.resolveDirectory(source.interpolatedValue).asFileTree
        }
      }
    }

    @Override
    protected void doInterpolate() {
      super.doInterpolate()
      preventBootstrapSudo.interpolate context
      version.interpolate context
      bootstrapCommand.interpolate context
      moduleDirs*.interpolate context
      module.interpolate context
      workingDirectory.interpolate context
      params.values()*.interpolate context
      preventSudo.interpolate context

      /*TODO bootstrap.interpolate context.withTemplateVariables([
        'Sudo': !preventBootstrapSudo.interpolatedValue,
        'Version': version.interpolatedValue,
      ])
      executeCommand.interpolate context.withTemplateVariables([
        'WorkingDirectory': workingDirectory.interpolatedValue,
        'Sudo': !preventSudo.interpolatedValue,
        'ParamsJSON': JsonOutput.toJson(params),
        'Module': module.interpolatedValue,
      ])*/
    }
  }
}
