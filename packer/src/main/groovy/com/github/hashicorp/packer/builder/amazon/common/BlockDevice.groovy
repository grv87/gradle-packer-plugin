/*
 * BlockDevice class
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
 *
 * Ported from original Packer code,
 * file builder/amazon/common/block_device.go
 * under the terms of the Mozilla Public License, v. 2.0.
 */
package com.github.hashicorp.packer.builder.amazon.common

import org.fidata.packer.engine.annotations.AutoImplement
import org.fidata.packer.engine.annotations.Default
import org.fidata.packer.engine.annotations.IgnoreIf
import org.fidata.packer.engine.annotations.OnlyIf
import org.fidata.packer.engine.annotations.PostProcess
import org.fidata.packer.engine.types.InterpolableBoolean
import org.fidata.packer.engine.types.InterpolableLong
import org.fidata.packer.engine.types.base.InterpolableObject
import org.fidata.packer.engine.types.InterpolableString
import groovy.transform.CompileStatic
import org.gradle.api.tasks.Input

@AutoImplement
@CompileStatic
abstract class BlockDevice implements InterpolableObject<BlockDevice> {
  @Input
  @Default({ Boolean.FALSE })
  @IgnoreIf({ -> noDevice.interpolated || virtualName.interpolated })
  abstract InterpolableBoolean getDeleteOnTermination()

  @Input
  abstract InterpolableString getDeviceName()

  @Input
  @Default({ Boolean.FALSE })
  @IgnoreIf({ -> noDevice.interpolated || virtualName.interpolated || snapshotId.interpolated })
  abstract InterpolableBoolean getEncrypted()

  @Input
  @OnlyIf({ -> volumeType.interpolated == VolumeType.IO1 /* TODO: Bug in either Packer or AWS documentation. This should be supported for gp2 volumes too */ })
  abstract InterpolableLong getIops()

  @Input
  @Default({ Boolean.FALSE })
  abstract InterpolableBoolean getNoDevice()

  @Input
  @IgnoreIf({ -> noDevice.interpolated || virtualName.interpolated })
  abstract InterpolableString getSnapshotId()

  @Input
  @IgnoreIf({ -> noDevice.interpolated })
  @PostProcess({ String interpolated -> interpolated.startsWith('ephemeral') ? null : interpolated })
  abstract InterpolableString getVirtualName()

  @Input
  @Default({ VolumeType.STANDARD })
  @IgnoreIf({ -> noDevice.interpolated || virtualName.interpolated })
  abstract InterpolableVolumeType getVolumeType()

  @Input
  // @Default() TODO: If you're creating the volume from a snapshot and don't specify a volume size, the default is the snapshot size.
  @IgnoreIf({ -> noDevice.interpolated || virtualName.interpolated })
  @PostProcess({ Long interpolableValue -> interpolableValue > 0 ? interpolableValue : null})
  abstract InterpolableLong getVolumeSize()

  @Input
  @IgnoreIf({ -> noDevice.interpolated || virtualName.interpolated })
  abstract InterpolableString getKmsKeyId()
}
