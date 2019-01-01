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
