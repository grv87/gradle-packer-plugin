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
package com.github.hashicorp.packer.provisioner

import org.fidata.packer.engine.AbstractEngine
import org.fidata.packer.engine.annotations.ComputedInput
import org.fidata.packer.engine.types.InterpolableFile
import org.fidata.packer.engine.types.InterpolableInputDirectory
import groovy.transform.CompileStatic
import com.github.hashicorp.packer.template.Provisioner
import org.fidata.packer.engine.types.InterpolableBoolean
import org.fidata.packer.engine.types.InterpolableString
import org.fidata.packer.engine.types.InterpolableStringArray
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.InputDirectory
import org.gradle.api.tasks.Internal
import org.gradle.api.tasks.Nested
import org.gradle.api.tasks.Optional

@CompileStatic
class ChefSolo extends Provisioner<Configuration> {
  static class Configuration extends Provisioner.Configuration {
    @Input
    InterpolableString chefEnvironment

    @Input
    InterpolableString configTemplate

    @Nested
    List<InterpolableInputDirectory> cookbookPaths

    @InputDirectory
    @Optional
    InterpolableFile rolesPath

    @InputDirectory
    @Optional
    InterpolableFile dataBagsPath

    @Internal
    InterpolableFile encryptedDataBagSecretPath

    @InputDirectory
    @Optional
    InterpolableFile environmentsPath

    @Input
    InterpolableString executeCommand

    @Input
    InterpolableString installCommand

    @Input
    InterpolableString remoteCookbookPaths

    @Input
    InterpolableString json // TOTEST

    @Internal
    InterpolableBoolean preventSudo

    @Input
    InterpolableStringArray runList

    @Input
    InterpolableBoolean skipInstall

    @Internal
    InterpolableString stagingDirectory

    @Internal
    InterpolableString guestOSType

    @Internal
    InterpolableString version

    @ComputedInput
    @Optional
    String getInterpolatedVersion() {
      skipInstall?.interpolatedValue ? null : version.interpolatedValue
    }

    @Override
    protected void doInterpolate() {
      super.doInterpolate()
      chefEnvironment.interpolate context
      configTemplate.interpolate context
      cookbookPaths*.interpolate context
      rolesPath.interpolate context
      dataBagsPath.interpolate context
      encryptedDataBagSecretPath.interpolate context
      environmentsPath.interpolate context
      executeCommand.interpolate context
      installCommand.interpolate context
      remoteCookbookPaths.interpolate context
      json.interpolate context
      preventSudo.interpolate context
      runList.interpolate context
      skipInstall.interpolate context
      stagingDirectory.interpolate context
      guestOSType.interpolate context
      version.interpolate context
    }
  }

  static void register(AbstractEngine engine) {
    engine.getSubtypeRegistry(Provisioner).registerSubtype 'chef-solo', this
  }
}
