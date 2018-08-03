/*
 * ChefSolo class
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

class ChefSolo extends Provisioner {
  @JsonProperty('chef_environment')
  String chefEnvironment
  @JsonProperty('config_template')
  String configTemplate
  @JsonProperty('cookbook_paths')
  List<String> cookbookPaths
  @JsonProperty('roles_path')
  String rolesPath
  @JsonProperty('data_bags_path')
  String dataBagsPath
  @JsonProperty('encrypted_data_bag_secret_path')
  String encryptedDataBagSecretPath
  @JsonProperty('environments_path')
  String environmentsPath
  @JsonProperty('execute_command')
  String executeCommand
  @JsonProperty('install_command')
  String installCommand
  @JsonProperty('remote_cookbook_paths')
  String remoteCookbookPaths
  Object json // TODO
  @JsonProperty('prevent_sudo')
  Boolean preventSudo
  @JsonProperty('run_list')
  List<String> runList
  @JsonProperty('skip_install')
  Boolean skipInstall
  @JsonProperty('staging_directory')
  String stagingDirectory
  @JsonProperty('guest_os_type')
  String guestOSType
  @JsonProperty('version')
  String version
}
