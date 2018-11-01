package com.github.hashicorp.packer.builder.amazon.common

import com.github.hashicorp.packer.engine.types.InterpolableBoolean
import com.github.hashicorp.packer.engine.types.InterpolableLong
import com.github.hashicorp.packer.engine.types.InterpolableObject
import com.github.hashicorp.packer.engine.types.InterpolableString
import groovy.transform.AutoClone
import groovy.transform.AutoCloneStyle
import groovy.transform.CompileStatic

@AutoClone(style = AutoCloneStyle.SIMPLE)
@CompileStatic
class BlockDevice extends InterpolableObject {
  InterpolableBoolean deleteOnTermination
  InterpolableString deviceName
  InterpolableBoolean encrypted
  InterpolableLong iops
  InterpolableBoolean noDevice
  InterpolableString snapshotId
  InterpolableString virtualName
  InterpolableString volumeType
  InterpolableLong volumeSize
  InterpolableString kmsKeyId
}
