/*
 * Shell class
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

import com.fasterxml.jackson.annotation.JsonProperty
import groovy.transform.AutoClone
import groovy.transform.AutoCloneStyle
import groovy.transform.CompileStatic
import org.fidata.gradle.packer.template.Provisioner

import java.time.Duration

@AutoClone(style = AutoCloneStyle.SIMPLE)
@CompileStatic
class Shell extends Provisioner<Configuration> {
  static class Configuration extends Provisioner.Configuration {
    Boolean binary

    List<String> inline

    @JsonProperty('inline_shebang')
    String inlineShebang

    String script

    List<String> scripts

    @JsonProperty('environment_vars')
    List<String> environmentVars

    @JsonProperty('remote_folder')
    String remoteFolder

    @JsonProperty('remote_file')
    String remoteFile

    @JsonProperty('remote_path') // TODO: defaults to remote_folder/remote_file
    String remotePath

    @JsonProperty('execute_command')
    List<String> executeCommand

    @JsonProperty('start_retry_timeout')
    Duration startRetryTimeout // TODO: parse Raw

    @JsonProperty('skip_clean')
    Boolean skipClean

    @JsonProperty('expect_disconnect')
    Boolean expectDisconnect

    @JsonProperty('use_linux_pathing')
    List<String> useLinuxPathing
  }
}
