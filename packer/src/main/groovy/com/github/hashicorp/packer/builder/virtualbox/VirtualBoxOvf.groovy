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
import com.github.hashicorp.packer.engine.annotations.AutoImplement
import com.github.hashicorp.packer.engine.annotations.Default
import com.github.hashicorp.packer.engine.annotations.Inline
import com.github.hashicorp.packer.engine.types.InterpolableBoolean
import com.github.hashicorp.packer.engine.types.InterpolableChecksumType
import com.github.hashicorp.packer.engine.types.InterpolableString
import com.github.hashicorp.packer.engine.types.InterpolableStringArray
import com.github.hashicorp.packer.engine.types.InterpolableVBoxGuestAdditionsMode
import com.github.hashicorp.packer.enums.VBoxGuestAdditionsMode
import groovy.transform.CompileStatic
import com.github.hashicorp.packer.template.Builder
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.Internal

@CompileStatic
@AutoImplement
abstract class VirtualBoxOvf extends Builder {
  @Inline
  abstract HTTPConfig getHttpConfig()

  @Inline
  abstract FloppyConfig getFloppyConfig()

  @Inline
  abstract BootConfig getBootConfig()

  @Inline
  abstract ExportConfig getExportConfig()

  @Inline
  abstract ExportOpts getExportOpts()

  @Inline
  abstract OutputConfig getOutputConfig()

  @Inline
  abstract RunConfig getRunConfig()

  @Inline
  abstract SSHConfig getSshConfig()

  @Inline
  abstract ShutdownConfig getShutdownConfig()

  @Inline
  abstract VBoxManageConfig getVboxManageConfig()

  @Inline
  abstract VBoxManagePostConfig getVboxManagePostConfig()

  @Inline
  abstract VBoxVersionConfig getVboxVersionConfig()

  @Input
  abstract InterpolableString getChecksum()

  @Input
  abstract InterpolableChecksumType getChecksumType()

  @Default({ VBoxGuestAdditionsMode.UPLOAD })
  abstract InterpolableVBoxGuestAdditionsMode getGuestAdditionsMode()

  abstract InterpolableString getGuestAdditionsPath()

  abstract InterpolableString getGuestAdditionsSHA256()

  abstract InterpolableString getGuestAdditionsURL()

  @Input
  abstract InterpolableStringArray getImportFlags()

  @Input
  abstract InterpolableString getImportOpts()

  abstract InterpolableString getSourcePath()

  abstract InterpolableString getTargetPath()

  @Internal // name of the OVF file for the new virtual machine, without the file extension
  @Default({ 'packer-{{ .BuildName }}' }) // TODO
  abstract InterpolableString getVmName()

  @Input
  @Default({ Boolean.FALSE })
  abstract InterpolableBoolean getKeepRegistered()

  @Input
  @Default({ Boolean.FALSE })
  abstract InterpolableBoolean getSkipExport() // TODO: handle
}
