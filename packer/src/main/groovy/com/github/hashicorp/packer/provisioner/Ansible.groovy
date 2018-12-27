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

import groovy.transform.CompileStatic
import com.github.hashicorp.packer.template.Provisioner
import org.fidata.packer.engine.types.InterpolableBoolean
import org.fidata.packer.engine.types.InterpolableString
import org.fidata.packer.engine.types.InterpolableStringArray
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.Internal

@CompileStatic
class Ansible extends Provisioner<Ansible.Configuration> { // TODO ??? IDEA is somewhere wrong
  static class Configuration extends Provisioner.Configuration {
    @Internal
    InterpolableString command

    @Input
    InterpolableStringArray extraArguments

    @Input
    InterpolableStringArray ansibleEnvVars

    @Input
    InterpolableString playbookFile

    @Input
    InterpolableStringArray groups
    InterpolableStringArray emptyGroups
    InterpolableString hostAlias

    @Internal
    InterpolableString user

    @Internal
    InterpolableString localPort

    @Internal
    InterpolableString sshHostKeyFile

    @Internal
    InterpolableString sshAuthorizedKeyFile

    @Internal
    InterpolableString sftpCmd
    InterpolableBoolean skipVersionCheck
    InterpolableBoolean useSFTP
    InterpolableString inventoryDirectory
    InterpolableString inventoryFile

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

      bootstrap.interpolate context.withTemplateVariables([
        'Sudo': !preventBootstrapSudo.interpolatedValue,
        'Version': version.interpolatedValue,
      ])
      executeCommand.interpolate context.withTemplateVariables([
        'WorkingDirectory': workingDirectory.interpolatedValue,
        'Sudo': !preventSudo.interpolatedValue,
        'ParamsJSON': JsonOutput.toJson(params),
        'Module': module.interpolatedValue,
      ])
    }
  }
}
