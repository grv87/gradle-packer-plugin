/*
 * VirtualBoxIso class
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
package org.fidata.gradle.packer.template.builder.virtualbox

import com.fasterxml.jackson.annotation.JsonUnwrapped
import groovy.transform.AutoClone
import groovy.transform.AutoCloneStyle
import groovy.transform.CompileStatic
import org.fidata.gradle.packer.template.Builder

@AutoClone(style = AutoCloneStyle.SIMPLE)
@CompileStatic
class VirtualBoxIso extends Builder {
  /*@JsonUnwrapped
  common.HTTPConfig               `mapstructure:",squash"`
  common.ISOConfig                `mapstructure:",squash"`
  common.FloppyConfig             `mapstructure:",squash"`
  bootcommand.BootConfig          `mapstructure:",squash"`
  vboxcommon.ExportConfig         `mapstructure:",squash"`
  vboxcommon.ExportOpts           `mapstructure:",squash"`
  vboxcommon.OutputConfig         `mapstructure:",squash"`
  vboxcommon.RunConfig            `mapstructure:",squash"`
  vboxcommon.ShutdownConfig       `mapstructure:",squash"`
  vboxcommon.SSHConfig            `mapstructure:",squash"`
  vboxcommon.VBoxManageConfig     `mapstructure:",squash"`
  vboxcommon.VBoxManagePostConfig `mapstructure:",squash"`
  vboxcommon.VBoxVersionConfig    `mapstructure:",squash"`

  UInte

  DiskSize               uint   `mapstructure:"disk_size"`
  GuestAdditionsMode     string `mapstructure:"guest_additions_mode"`
  GuestAdditionsPath     string `mapstructure:"guest_additions_path"`
  GuestAdditionsSHA256   string `mapstructure:"guest_additions_sha256"`
  GuestAdditionsURL      string `mapstructure:"guest_additions_url"`
  GuestOSType            string `mapstructure:"guest_os_type"`
  HardDriveDiscard       bool   `mapstructure:"hard_drive_discard"`
  HardDriveInterface     string `mapstructure:"hard_drive_interface"`
  SATAPortCount          int    `mapstructure:"sata_port_count"`
  HardDriveNonrotational bool   `mapstructure:"hard_drive_nonrotational"`
  ISOInterface           string `mapstructure:"iso_interface"`
  KeepRegistered         bool   `mapstructure:"keep_registered"`
  SkipExport             bool   `mapstructure:"skip_export"`
  VMName                 string `mapstructure:"vm_name"`*/
}
