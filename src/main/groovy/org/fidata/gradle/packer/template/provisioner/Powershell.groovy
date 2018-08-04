/*
 * Powershell class
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
import org.fidata.gradle.packer.template.Provisioner

import java.time.Duration

class Powershell implements Provisioner {
  Boolean binary

  List<String> inline

  String script

  List<String> scripts

  @JsonProperty('environment_vars')
  List<String> environmentVars

  @JsonProperty('remote_path')
  String remotePath

  @JsonProperty('remote_env_var_path')
  String remoteEnvVarPath

  @JsonProperty('execute_command')
  String executeCommand

  @JsonProperty('elevated_execute_command')
  String elevatedExecuteCommand

  @JsonProperty('start_retry_timeout')
  Duration startRetryTimeout

  @JsonProperty('elevated_user')
  String elevatedUser

  @JsonProperty('elevated_password')
  String elevatedPassword

  @JsonProperty('valid_exit_codes')
  List<Integer> validExitCodes
}
