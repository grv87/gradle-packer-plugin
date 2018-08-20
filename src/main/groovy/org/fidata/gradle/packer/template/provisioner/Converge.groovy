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
package org.fidata.gradle.packer.template.provisioner

import groovy.json.JsonOutput
import groovy.transform.AutoClone
import groovy.transform.AutoCloneStyle
import groovy.transform.CompileStatic
import org.fidata.gradle.packer.template.Provisioner
import org.fidata.gradle.packer.template.internal.InterpolableObject
import org.fidata.gradle.packer.template.types.InterpolableBoolean
import org.fidata.gradle.packer.template.types.InterpolableFile
import org.fidata.gradle.packer.template.types.InterpolableString
import org.fidata.gradle.packer.template.types.InterpolableStringArray
import org.gradle.api.file.ConfigurableFileTree
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.Internal
import org.gradle.api.tasks.Nested

@AutoClone(style = AutoCloneStyle.SIMPLE)
@CompileStatic
class Converge extends Provisioner<Configuration> {
  static class Configuration extends Provisioner.Configuration {
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
      InterpolableFile source

      @Input
      InterpolableString destination

      @Internal
      InterpolableStringArray exclude

      // TODO

      @Override
      protected void doInterpolate() {
        source.interpolate context
        destination.interpolate context
        exclude.interpolate context
        if (exclude?.interpolatedValue?.size() > 0) {
          context.task.inputs.files(context.task.project.fileTree(source) { ConfigurableFileTree configurableFileTree ->
            configurableFileTree.exclude *exclude
          })
        } else {
          context.task.inputs.dir source
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
      params.values*.interpolate context
      preventSudo.interpolate context

      bootstrap.interpolate context.addTemplateVariables([
        'Sudo': !preventBootstrapSudo.interpolatedValue,
        'Version': version.interpolatedValue,
      ])
      executeCommand.interpolate context.addTemplateVariables([
        'WorkingDirectory': workingDirectory.interpolatedValue,
        'Sudo': !preventSudo.interpolatedValue,
        'ParamsJSON': JsonOutput.toJson(params),
        'Module': module.interpolatedValue,
      ])
    }
  }
}
