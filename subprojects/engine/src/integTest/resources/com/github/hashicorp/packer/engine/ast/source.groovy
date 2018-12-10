package com.github.hashicorp.packer.engine.ast

import com.github.hashicorp.packer.engine.annotations.AutoImplement
import com.github.hashicorp.packer.engine.annotations.Default
import com.github.hashicorp.packer.engine.annotations.IgnoreIf
import com.github.hashicorp.packer.engine.annotations.PostProcess
import com.github.hashicorp.packer.engine.types.InterpolableBoolean
import com.github.hashicorp.packer.engine.types.InterpolableLong
import com.github.hashicorp.packer.engine.types.InterpolableObject
import com.github.hashicorp.packer.engine.types.InterpolableString
import groovy.transform.CompileStatic
import org.gradle.api.tasks.Input

@AutoImplement
@CompileStatic
interface BlockDevice extends InterpolableObject<BlockDevice> {
  @Input
  @Default({ Boolean.FALSE })
  @IgnoreIf({ noDevice.interpolated || virtualName.interpolated })
  InterpolableBoolean getDeleteOnTermination()

  @Input
  InterpolableString getDeviceName()

  @Input
  @Default({ Boolean.FALSE })
  @IgnoreIf({ noDevice.interpolated || virtualName.interpolated || snapshotId.interpolated })
  InterpolableBoolean getEncrypted()

  @Input
  @Default({ Boolean.FALSE })
  InterpolableBoolean getNoDevice()

  @Input
  @IgnoreIf({ noDevice.interpolated || virtualName.interpolated })
  InterpolableString getSnapshotId()

  @Input
  @IgnoreIf({ noDevice.interpolated })
  @PostProcess({ String interpolated -> interpolated.startsWith('ephemeral') ? null : interpolated })
  InterpolableString getVirtualName()

  @Input
  @IgnoreIf({ noDevice.interpolated || virtualName.interpolated })
  InterpolableString getKmsKeyId()
}
