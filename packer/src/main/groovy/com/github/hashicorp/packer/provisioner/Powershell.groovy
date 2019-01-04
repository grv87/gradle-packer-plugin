/*
 * Powershell provisioner
 * Copyright © 2018-2019  Basil Peace
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
 *
 * Ported from original Packer code,
 * file provisioner/powershell/provisioner.go
 * under the terms of the Mozilla Public License, v. 2.0.
 */
package com.github.hashicorp.packer.provisioner

import com.fasterxml.jackson.annotation.JsonProperty
import org.fidata.packer.engine.AbstractEngine
import org.fidata.packer.engine.annotations.AutoImplement
import org.fidata.packer.engine.annotations.Timing
import org.fidata.packer.engine.types.InterpolableDuration
import org.fidata.packer.engine.types.InterpolableFile
import org.fidata.packer.engine.types.InterpolableInteger
import groovy.transform.CompileStatic
import com.github.hashicorp.packer.template.Provisioner
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.InputFile
import org.gradle.api.tasks.InputFiles
import org.gradle.api.tasks.Internal
import org.gradle.api.tasks.Optional
import org.gradle.api.tasks.PathSensitive
import org.gradle.api.tasks.PathSensitivity

@CompileStatic
class Powershell extends Provisioner<Config> {
  @AutoImplement
  abstract static class Config extends Provisioner.Config<Config> {
    @Internal
    abstract Boolean getBinary()

    @Input
    @Optional
    abstract List<String> getInline()

    @InputFile
    @PathSensitive(PathSensitivity.NONE) // TODOC
    @Optional
    abstract InterpolableFile getScript()

    @InputFiles
    @PathSensitive(PathSensitivity.NONE) // TODOC
    @Optional
    abstract List<InterpolableFile> getScripts()

    @JsonProperty('environment_vars')
    @Input
    @Optional
    abstract List<String> getVars()

    @Internal
    abstract String getRemotePath()

    @Internal
    abstract String getRemoteEnvVarPath()

    @Input // TODO
    abstract String getExecuteCommand()

    @Input // TODO
    abstract String getElevatedExecuteCommand()

    @Timing
    abstract InterpolableDuration getStartRetryTimeout()

    @Internal
    abstract String getElevatedEnvVarFormat()

    @Input
    abstract String getElevatedUser()

    @Internal
    abstract String getElevatedPassword()

    @Internal
    abstract List<InterpolableInteger> getValidExitCodes()
  }

  static void register(AbstractEngine engine) {
    engine.registerSubtype Provisioner, 'powershell', this
  }
}
