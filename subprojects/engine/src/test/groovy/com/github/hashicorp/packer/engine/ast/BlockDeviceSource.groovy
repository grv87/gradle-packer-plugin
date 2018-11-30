package com.github.hashicorp.packer.engine.ast

import com.github.hashicorp.packer.engine.annotations.Default
import com.github.hashicorp.packer.engine.annotations.IgnoreIf
import com.github.hashicorp.packer.engine.annotations.PostProcess
import com.github.hashicorp.packer.engine.types.InterpolableBoolean
import com.github.hashicorp.packer.engine.types.InterpolableLong
import com.github.hashicorp.packer.engine.types.InterpolableObject
import com.github.hashicorp.packer.engine.types.InterpolableString
import groovy.transform.CompileStatic
import org.gradle.api.tasks.Input

@CompileStatic
abstract class BlockDeviceSource implements InterpolableObject<BlockDeviceSource> {
  @Input
  @Default({ Boolean.FALSE })
  @IgnoreIf({ noDevice.get() || virtualName.get() })
  abstract InterpolableBoolean getDeleteOnTermination()

  @Input
  abstract InterpolableString getDeviceName()

  @Input
  @Default({ Boolean.FALSE })
  @IgnoreIf({ noDevice.get() || virtualName.get() || snapshotId.get() })
  abstract InterpolableBoolean getEncrypted()

  @Input
  @IgnoreIf({ volumeType.get() != 'io1' /* TODO: Bug in either packer or AWS documentation. This should be supported for gp2 volumes too */ })
  abstract InterpolableLong getIops()

  @Input
  @Default({ Boolean.FALSE })
  abstract InterpolableBoolean getNoDevice()

  @Input
  @IgnoreIf({ noDevice.get() || virtualName.get() })
  abstract InterpolableString getSnapshotId()

  @Input
  @IgnoreIf({ noDevice.get() })
  @PostProcess({ String interpolatedValue -> interpolatedValue.startsWith('ephemeral') ? null : interpolatedValue })
  abstract InterpolableString getVirtualName()

  @Input
  @Default({ 'standard' })
  @IgnoreIf({ noDevice.get() || virtualName.get() })
  abstract InterpolableString getVolumeType()

  @Input
  // @Default() TODO: If you're creating the volume from a snapshot and don't specify a volume size, the default is the snapshot size.
  @IgnoreIf({ noDevice.get() || virtualName.get() })
  @PostProcess({ Long interpolableValue -> interpolableValue > 0 ? interpolableValue : null})
  abstract InterpolableLong getVolumeSize()

  @Input
  @IgnoreIf({ noDevice.get() || virtualName.get() })
  abstract InterpolableString getKmsKeyId()
}
