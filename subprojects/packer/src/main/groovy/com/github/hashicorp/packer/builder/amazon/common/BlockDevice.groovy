package com.github.hashicorp.packer.builder.amazon.common

import com.github.hashicorp.packer.engine.annotations.ComputedInput
import com.github.hashicorp.packer.engine.types.InterpolableBoolean
import com.github.hashicorp.packer.engine.types.InterpolableLong
import com.github.hashicorp.packer.engine.types.InterpolableObject
import com.github.hashicorp.packer.engine.types.InterpolableString
import com.github.hashicorp.packer.template.Context
import groovy.transform.AutoClone
import groovy.transform.AutoCloneStyle
import groovy.transform.CompileStatic
import groovy.transform.Synchronized
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.Internal
import org.gradle.api.tasks.Optional

@AutoClone(style = AutoCloneStyle.SIMPLE)
@CompileStatic
// @Builder(builderStrategy = ExternalStrategy, forClass = BlockDevice)
// @JsonDeserialize(builder = BlockDeviceBuilder) // TOTEST
final class BlockDevice implements InterpolableObject<BlockDevice> {
  @Internal // TOTEST
  private InterpolableBoolean deleteOnTermination

  @ComputedInput
  @Synchronized
  InterpolableBoolean getDeleteOnTermination() {
    if (InterpolableBoolean.Utils.requiresInitialization(deleteOnTermination)) {
      deleteOnTermination = InterpolableBoolean.Utils.initWithDefault(deleteOnTermination, Boolean.FALSE, { noDevice.get() || virtualName.get() } )
    }
    deleteOnTermination
  }

  @Internal
  InterpolableString deviceName

  @ComputedInput
  @Synchronized
  InterpolableString getDeviceName() {
    if (InterpolableString.Utils.requiresInitialization(deviceName)) {
      deviceName = InterpolableString.Utils.initWithDefault(deviceName, (String)null)
    }
    deviceName
  }

  @Internal
  private InterpolableBoolean encrypted

  @ComputedInput
  @Synchronized
  InterpolableBoolean getEncrypted() {
    if (InterpolableBoolean.Utils.requiresInitialization(encrypted)) {
      encrypted = InterpolableBoolean.Utils.initWithDefault(encrypted, Boolean.FALSE, { noDevice.get() || virtualName.get() || snapshotId.get() } )
    }
    encrypted
  }

  @Internal
  private InterpolableLong iops

  @ComputedInput
  @Synchronized
  InterpolableLong getIops() {
    if (InterpolableLong.Utils.requiresInitialization(iops)) {
      iops = InterpolableLong.Utils.initWithDefault(iops, (Long)null, { volumeType.get() != 'io1' /* TODO: Bug in either packer or AWS documentation. This should be supported for gp2 volumes too */ } )
    }
    iops
  }

  @Internal
  private InterpolableBoolean noDevice

  @ComputedInput
  @Synchronized
  InterpolableBoolean getNoDevice() {
    if (InterpolableBoolean.Utils.requiresInitialization(noDevice)) {
      noDevice = InterpolableBoolean.Utils.initWithDefault(noDevice, Boolean.FALSE)
    }
    noDevice
  }

  @Internal
  private InterpolableString snapshotId

  @ComputedInput
  @Synchronized
  InterpolableString getSnapshotId() {
    if (InterpolableString.Utils.requiresInitialization(snapshotId)) {
      snapshotId = InterpolableString.Utils.initWithDefault(snapshotId, (String)null, { noDevice.get() || virtualName.get() }, { String interpolatedValue -> interpolatedValue.empty ? null : interpolatedValue } )
    }
    snapshotId
  }

  @Internal
  private InterpolableString virtualName

  @ComputedInput
  @Synchronized
  InterpolableString getVirtualName() {
    if (InterpolableString.Utils.requiresInitialization(virtualName)) {
      virtualName = InterpolableString.Utils.initWithDefault(virtualName, (String)null, { noDevice.get() }, { String interpolatedValue -> interpolatedValue.startsWith('ephemeral') ? null : interpolatedValue } )
    }
    virtualName
  }

  @Internal
  private InterpolableString volumeType

  @ComputedInput
  @Synchronized
  InterpolableString getVolumeType() {
    if (InterpolableString.Utils.requiresInitialization(volumeType)) {
      volumeType = InterpolableString.Utils.initWithDefault(volumeType, 'standard', { noDevice.get() || virtualName.get() } )
    }
    volumeType
  }

  @Internal
  private InterpolableLong volumeSize // TODO: If you're creating the volume from a snapshot and don't specify a volume size, the default is the snapshot size.

  @ComputedInput
  @Synchronized
  InterpolableLong getVolumeSize() {
    if (InterpolableLong.Utils.requiresInitialization(volumeSize)) {
      volumeSize = InterpolableLong.Utils.initWithDefault(volumeSize, (Long)null, { noDevice.get() || virtualName.get() }, { Long interpolableValue -> interpolableValue > 0 ? interpolableValue : null} )
    }
    volumeSize
  }

  @Optional
  private InterpolableString kmsKeyId

  @Override
  BlockDevice interpolate(Context context) {
    BlockDevice result = new BlockDevice()
    result.@deleteOnTermination = deleteOnTermination.interpolateValue(context, result)
    result.@deviceName = deviceName.interpolateValue(context, result)
    result.@encrypted = encrypted.interpolateValue(context, result)
    result.@iops = iops.interpolateValue(context, result)
    result.@noDevice = noDevice.interpolateValue(context, result)
    result.@snapshotId = snapshotId.interpolateValue(context, result)
    result.@virtualName = virtualName.interpolateValue(context, result)
    result.@volumeType = volumeType.interpolateValue(context, result)
    result.@volumeSize = volumeSize.interpolateValue(context, result)
    result.@kmsKeyId = kmsKeyId.interpolateValue(context, result)
    result
  }
}
