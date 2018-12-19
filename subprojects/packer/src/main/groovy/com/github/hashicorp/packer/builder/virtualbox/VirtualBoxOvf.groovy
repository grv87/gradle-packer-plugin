/*
 * VirtualBoxOvf class
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
package com.github.hashicorp.packer.builder.virtualbox

import com.github.hashicorp.packer.builder.virtualbox.common.ExportConfig
import com.github.hashicorp.packer.builder.virtualbox.common.ExportOpts
import com.github.hashicorp.packer.builder.virtualbox.common.OutputConfig
import com.github.hashicorp.packer.builder.virtualbox.common.RunConfig
import com.github.hashicorp.packer.builder.virtualbox.common.SSHConfig
import com.github.hashicorp.packer.builder.virtualbox.common.ShutdownConfig
import com.github.hashicorp.packer.builder.virtualbox.common.VBoxManageConfig
import com.github.hashicorp.packer.builder.virtualbox.common.VBoxManagePostConfig
import com.github.hashicorp.packer.builder.virtualbox.common.VBoxVersionConfig
import com.github.hashicorp.packer.common.FloppyConfig
import com.github.hashicorp.packer.common.HTTPConfig
import com.github.hashicorp.packer.common.bootcommand.BootConfig
import com.github.hashicorp.packer.engine.annotations.Inline
import com.github.hashicorp.packer.engine.enums.VBoxGuestAdditionsMode
import com.github.hashicorp.packer.engine.types.InterpolableBoolean
import com.github.hashicorp.packer.engine.enums.InterpolableChecksumType
import com.github.hashicorp.packer.engine.types.InterpolableString
import com.github.hashicorp.packer.engine.types.InterpolableStringArray
import com.github.hashicorp.packer.engine.enums.InterpolableVBoxGuestAdditionsMode
import groovy.transform.CompileStatic
import com.github.hashicorp.packer.template.Builder
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.Internal

@CompileStatic
class VirtualBoxOvf extends Builder {
  @Inline
  HTTPConfig httpConfig

  @Inline
  FloppyConfig floppyConfig

  @Inline
  BootConfig bootConfig

  @Inline
  ExportConfig exportConfig

  @Inline
  ExportOpts exportOpts

  @Inline
  OutputConfig outputConfig

  @Inline
  RunConfig runConfig

  @Inline
  SSHConfig sshConfig

  @Inline
  ShutdownConfig shutdownConfig

  @Inline
  VBoxManageConfig vboxManageConfig

  @Inline
  VBoxManagePostConfig vboxManagePostConfig

  @Inline
  VBoxVersionConfig vboxVersionConfig

  @Input
  InterpolableString checksum

  @Input
  InterpolableChecksumType checksumType

  InterpolableVBoxGuestAdditionsMode guestAdditionsMode = InterpolableVBoxGuestAdditionsMode.withDefault(VBoxGuestAdditionsMode.UPLOAD)

  InterpolableString guestAdditionsPath

  InterpolableString guestAdditionsSHA256

  InterpolableString guestAdditionsURL

  @Input
  InterpolableStringArray importFlags

  @Input
  InterpolableString importOpts

  InterpolableString sourcePath

  InterpolableString targetPath

  @Internal // name of the OVF file for the new virtual machine, without the file extension
  // @Default(value = 'packer-BUILDNAME') // TODO
  InterpolableString vmName

  @Input
  InterpolableBoolean keepRegistered = InterpolableBoolean.withDefault(false)

  @Input
  InterpolableBoolean skipExport = InterpolableBoolean.withDefault(false) // TODO: handle
}
