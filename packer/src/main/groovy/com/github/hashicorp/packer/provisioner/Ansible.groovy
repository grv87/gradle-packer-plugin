/*
 * Ansible provisioner
 * Copyright ©  Basil Peace
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
 * file provisioner/ansible/provisioner.go
 * under the terms of the Mozilla Public License, v. 2.0.
 */
package com.github.hashicorp.packer.provisioner

import groovy.transform.CompileStatic
import com.github.hashicorp.packer.template.Provisioner
import org.fidata.packer.engine.AbstractEngine
import org.fidata.packer.engine.annotations.AutoImplement
import org.fidata.packer.engine.annotations.ConnectionSetting
import org.fidata.packer.engine.annotations.Credential
import org.fidata.packer.engine.annotations.Default
import org.fidata.packer.engine.types.InterpolableBoolean
import org.fidata.packer.engine.types.InterpolableString
import org.fidata.packer.engine.types.InterpolableStringArray
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.Internal

@CompileStatic
class Ansible extends Provisioner<Config> { // TODO ??? IDEA is somewhere wrong
  @AutoImplement
  abstract static class Config extends Provisioner.Config<Config> {
    @Internal
    abstract InterpolableString getCommand()

    @Input
    abstract InterpolableStringArray getExtraArguments()

    @Input
    abstract InterpolableStringArray getAnsibleEnvVars()

    @Input
    abstract InterpolableString getPlaybookFile()

    @Input
    abstract InterpolableStringArray getGroups()
    abstract InterpolableStringArray getEmptyGroups()
    abstract InterpolableString getHostAlias()

    @Credential
    abstract InterpolableString getUser()

    @ConnectionSetting
    abstract InterpolableString getLocalPort()

    @ConnectionSetting
    abstract InterpolableString getSshHostKeyFile()

    @ConnectionSetting
    abstract InterpolableString getSshAuthorizedKeyFile()

    @Internal
    @Default({ '/usr/lib/sftp-server -e' })
    abstract InterpolableString getSftpCmd()
    abstract InterpolableBoolean getSkipVersionCheck()
    abstract InterpolableBoolean getUseSFTP()
    abstract InterpolableString getInventoryDirectory()
    abstract InterpolableString getInventoryFile()
  }

  static void register(AbstractEngine engine) {
    engine.registerSubtype Provisioner, 'ansible', this
  }
}
