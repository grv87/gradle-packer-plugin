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

import groovy.json.JsonOutput
import org.fidata.packer.engine.AbstractEngine
import org.fidata.packer.engine.annotations.AutoImplement
import org.fidata.packer.engine.annotations.ComputedInputFiles
import groovy.transform.CompileStatic
import com.github.hashicorp.packer.template.Provisioner
import org.fidata.packer.engine.annotations.ContextVar
import org.fidata.packer.engine.annotations.ContextVars
import org.fidata.packer.engine.annotations.Default
import org.fidata.packer.engine.annotations.ExtraProcessed
import org.fidata.packer.engine.types.base.InterpolableObject
import org.fidata.packer.engine.types.InterpolableBoolean
import org.fidata.packer.engine.types.InterpolableString
import org.fidata.packer.engine.types.InterpolableStringArray
import org.gradle.api.file.ConfigurableFileTree
import org.gradle.api.file.FileTree
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.Nested

@CompileStatic
class Converge extends Provisioner<Configuration> {
  @AutoImplement
  abstract static class Configuration extends Provisioner.Configuration implements InterpolableObject<Configuration> {
    @Input
    @ContextVars([
      @ContextVar(key = 'Sudo', value = { preventBootstrapSudo.interpolated }),
      @ContextVar(key = 'Version', value = { version.interpolated }),
    ])
    @Default({ Boolean.TRUE })
    abstract InterpolableBoolean getBootstrap()

    @Input
    abstract InterpolableString getVersion()

    @Input
    abstract InterpolableString getBootstrapCommand()

    @Input
    abstract InterpolableBoolean getPreventBootstrapSudo()

    @Nested
    abstract List<ModuleDir> getModuleDirs()

    @Input
    abstract InterpolableString getModule()

    @Input
    abstract InterpolableString getWorkingDirectory()

    @Input
    abstract Map<String, InterpolableString> getParams()

    @Input
    @ContextVars([ // TODO
      @ContextVar(key = 'WorkingDirectory', value = { workingDirectory.interpolated }),
      @ContextVar(key = 'Sudo', value = { preventSudo.interpolated }),
      @ContextVar(key = 'ParamsJSON', value = { new JsonOutput().toJson(params) }),
      @ContextVar(key = 'Module', value = { module.interpolated }),
    ])
    abstract InterpolableString getExecuteCommand()

    @Input
    abstract InterpolableBoolean getPreventSudo()

    @AutoImplement
    abstract static class ModuleDir implements InterpolableObject<ModuleDir> {
      @ExtraProcessed
      abstract InterpolableString/*File*/ getSource()

      @Input
      abstract InterpolableString getDestination()

      @ExtraProcessed
      abstract InterpolableStringArray getExclude()

      // TODO

      @ComputedInputFiles
      FileTree inputFileTree

      @Override
      protected void doInterpolate() {
        source.interpolate context
        destination.interpolate context
        exclude.interpolate context
        if (exclude?.get()?.empty == false) {
          inputFileTree = context.resolveFileTree(source.get()) { ConfigurableFileTree configurableFileTree ->
            configurableFileTree.exclude exclude.get()
          }
        } else {
          /*
           * CAVEAT:
           * We make shortcut and save input directory as file tree, to save some code.
           * There could be some troubles with this approach
           */
          inputFileTree = context.resolveDirectory(source.get()).asFileTree
        }
      }
    }
  }

  static void register(AbstractEngine engine) {
    engine.getSubtypeRegistry(Provisioner).registerSubtype 'converge', this
  }
}
