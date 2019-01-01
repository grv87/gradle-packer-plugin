/*
 * ChefSolo class
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
 */
package com.github.hashicorp.packer.provisioner

import org.fidata.packer.engine.AbstractEngine
import org.fidata.packer.engine.annotations.AutoImplement
import org.fidata.packer.engine.annotations.Default
import org.fidata.packer.engine.annotations.ExtraProcessed
import org.fidata.packer.engine.annotations.IgnoreIf
import org.fidata.packer.engine.annotations.OnlyIf
import org.fidata.packer.engine.annotations.Staging
import org.fidata.packer.engine.types.InterpolableFile
import groovy.transform.CompileStatic
import com.github.hashicorp.packer.template.Provisioner
import org.fidata.packer.engine.types.InterpolableBoolean
import org.fidata.packer.engine.types.InterpolableFileContent
import org.fidata.packer.engine.types.InterpolableString
import org.fidata.packer.engine.types.InterpolableStringArray
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.InputDirectory
import org.gradle.api.tasks.Internal
import org.gradle.api.tasks.Optional
import org.gradle.api.tasks.PathSensitive
import org.gradle.api.tasks.PathSensitivity

@CompileStatic
class ChefSolo extends Provisioner<Configuration> {
  @AutoImplement
  abstract static class Configuration extends Provisioner.Configuration<Configuration> {
    /**
     * The name of the chef_environment sent to the Chef server.
     * By default this is empty and will not use an environment
     * @return The name of the chef_environment
     */
    @Input
    @OnlyIf({ -> environmentsPath.interpolated })
    abstract InterpolableString getChefEnvironment()

    @Input
    @Default({
      '''\
        cookbook_path [{{.CookbookPaths}}]
        {{if .HasRolesPath}}
        role_path "{{.RolesPath}}"
        {{end}}
        {{if .HasDataBagsPath}}
        data_bag_path	"{{.DataBagsPath}}"
        {{end}}
        {{if .HasEncryptedDataBagSecretPath}}
        encrypted_data_bag_secret "{{.EncryptedDataBagSecretPath}}"
        {{end}}
        {{if .HasEnvironmentsPath}}
        environment_path "{{.EnvironmentsPath}}"
        environment "{{.ChefEnvironment}}"
        {{end}}
      '''.stripIndent()
    })
    abstract InterpolableFileContent getConfigTemplate()

    @ExtraProcessed
    abstract List<InterpolableFile> getCookbookPaths()

    @InputDirectory
    @PathSensitive(PathSensitivity.RELATIVE)
    @Optional
    abstract InterpolableFile getRolesPath()

    @InputDirectory
    @PathSensitive(PathSensitivity.RELATIVE)
    @Optional
    abstract InterpolableFile getDataBagsPath()

    @ExtraProcessed
    abstract InterpolableFile getEncryptedDataBagSecretPath()

    @InputDirectory
    @PathSensitive(PathSensitivity.RELATIVE)
    @Optional
    abstract InterpolableFile getEnvironmentsPath()

    @Input
    abstract InterpolableString getExecuteCommand()

    @Input
    abstract InterpolableString getInstallCommand()

    @Input
    abstract InterpolableString getRemoteCookbookPaths()

    @Input
    abstract InterpolableString getJson() // TOTEST

    @Internal
    abstract InterpolableBoolean getPreventSudo()

    @Input
    abstract InterpolableStringArray getRunList()

    @Input
    abstract InterpolableBoolean getSkipInstall()

    @Staging
    abstract InterpolableString getStagingDirectory()

    @Internal
    abstract InterpolableString getGuestOSType()

    @Input
    @Optional
    @IgnoreIf({ -> skipInstall.interpolatedValue != Boolean.TRUE })
    abstract InterpolableString getVersion()
  }

  static void register(AbstractEngine engine) {
    engine.registerSubtype Provisioner, 'chef-solo', this
  }
}
