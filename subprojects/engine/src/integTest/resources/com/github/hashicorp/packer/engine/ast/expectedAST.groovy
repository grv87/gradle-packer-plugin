package com.github.hashicorp.packer.engine.ast

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty
// import com.fasterxml.jackson.annotation.JsonValue
import com.github.hashicorp.packer.engine.types.InterpolableBoolean
// import com.github.hashicorp.packer.engine.types.InterpolableEnum
import com.github.hashicorp.packer.engine.types.InterpolableLong
import com.github.hashicorp.packer.engine.types.InterpolableObject
import com.github.hashicorp.packer.engine.types.InterpolableString
import com.github.hashicorp.packer.template.Context
import groovy.transform.CompileStatic
// import groovy.transform.InheritConstructors
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.Optional
import com.fasterxml.jackson.databind.annotation.JsonDeserialize

@JsonDeserialize(as = BlockDeviceImpl)
@CompileStatic
interface BlockDevice extends InterpolableObject<BlockDevice> {
  @JsonProperty('delete_on_termination')
  @Input
  @Optional
  InterpolableBoolean getDeleteOnTermination()

  @JsonProperty('device_name')
  @Input
  InterpolableString getDeviceName()

  @JsonProperty('encrypted')
  @Input
  @Optional
  InterpolableBoolean getEncrypted()

  @JsonProperty('iops')
  @Input
  @Optional
  InterpolableLong getIops()

  @JsonProperty('no_device')
  @Input
  InterpolableBoolean getNoDevice()

  @JsonProperty('snapshot_id')
  @Input
  @Optional
  InterpolableString getSnapshotId()

  @JsonProperty('virtual_name')
  @Input
  @Optional
  InterpolableString getVirtualName()

  @JsonProperty('volume_type')
  @Input
  @Optional
  /*InterpolableVolumeType*/ InterpolableString getVolumeType()

  @JsonProperty('volume_size')
  @Input
  @Optional
  InterpolableLong getVolumeSize()

  @JsonProperty('kms_key_id')
  @Input
  @Optional
  InterpolableString getKmsKeyId()

  static final class BlockDeviceImpl implements BlockDevice {
    private final InterpolableBoolean deleteOnTermination

    @Override
    InterpolableBoolean getDeleteOnTermination() {
      this.@deleteOnTermination
    }

    private final InterpolableString deviceName

    @Override
    InterpolableString getDeviceName() {
      this.@deviceName
    }

    private final InterpolableBoolean encrypted

    @Override
    InterpolableBoolean getEncrypted() {
      this.@encrypted
    }

    private final InterpolableLong iops

    @Override
    InterpolableLong getIops() {
      this.@iops
    }

    private final InterpolableBoolean noDevice

    @Override
    InterpolableBoolean getNoDevice() {
      this.@noDevice
    }

    private final InterpolableString snapshotId

    @Override
    InterpolableString getSnapshotId() {
      this.@snapshotId
    }

    private final InterpolableString virtualName

    @Override
    InterpolableString getVirtualName() {
      this.@virtualName
    }

    private final /*InterpolableVolumeType*/ InterpolableString volumeType

    @Override
    /*InterpolableVolumeType*/ InterpolableString getVolumeType() {
      this.@volumeType
    }

    private final InterpolableLong volumeSize

    @Override
    InterpolableLong getVolumeSize() {
      this.@volumeSize
    }

    private final InterpolableString kmsKeyId

    @Override
    InterpolableString getKmsKeyId() {
      this.@kmsKeyId
    }

    @JsonCreator
    BlockDeviceImpl(
      @JsonProperty('delete_on_termination') InterpolableBoolean deleteOnTermination,
      @JsonProperty('device_name') InterpolableString deviceName,
      @JsonProperty('encrypted') InterpolableBoolean encrypted,
      @JsonProperty('iops') InterpolableLong iops,
      @JsonProperty('no_device') InterpolableBoolean noDevice,
      @JsonProperty('snapshot_id') InterpolableString snapshotId,
      @JsonProperty('virtual_name') InterpolableString virtualName,
      @JsonProperty('volume_type') /*InterpolableVolumeType*/ InterpolableString volumeType,
      @JsonProperty('volume_size') InterpolableLong volumeSize,
      @JsonProperty('kms_key_id') InterpolableString kmsKeyId
    ) {
      this.@deleteOnTermination = deleteOnTermination ?: new InterpolableBoolean.ImmutableRaw()
      this.@deviceName = deviceName ?: new InterpolableString.ImmutableRaw()
      this.@encrypted = encrypted ?: new InterpolableBoolean.ImmutableRaw()
      this.@iops = iops ?: new InterpolableLong.ImmutableRaw()
      this.@noDevice = noDevice ?: new InterpolableBoolean.ImmutableRaw()
      this.@snapshotId = snapshotId ?: new InterpolableString.ImmutableRaw()
      this.@virtualName = virtualName ?: new InterpolableString.ImmutableRaw()
      this.@volumeType = volumeType ?: new /*InterpolableVolumeType*/ InterpolableString.ImmutableRaw()
      this.@volumeSize = volumeSize ?: new InterpolableLong.ImmutableRaw()
      this.@kmsKeyId = kmsKeyId ?: new InterpolableString.ImmutableRaw()
    }

    private BlockDeviceImpl(Context context, BlockDevice from) {
      this.@deleteOnTermination = from.deleteOnTermination.interpolateValue(context, Boolean.FALSE, {
        noDevice.interpolated || virtualName.interpolated
      })
      this.@deviceName = from.deviceName.interpolateValue(context)
      this.@encrypted = from.encrypted.interpolateValue(context, Boolean.FALSE, {
        noDevice.interpolated || virtualName.interpolated || snapshotId.interpolated
      })
      this.@iops = from.iops.interpolateValue(context, (Long)null, {
        /* TODO: Bug in either packer or AWS documentation. This should be supported for gp2 volumes too */
        volumeType.interpolated != /*VolumeType.IO1*/ 'io1'
      })
      this.@noDevice = from.noDevice.interpolateValue(context, Boolean.FALSE)
      this.@snapshotId = from.snapshotId.interpolateValue(context, (String)null, {
        noDevice.interpolated || virtualName.interpolated
      })
      this.@virtualName = from.virtualName.interpolateValue(context, (String)null, {
        noDevice.interpolated
      }) { String interpolated ->
        interpolated.startsWith('ephemeral') ? null : interpolated
      }
      this.@volumeType = from.volumeType.interpolateValue(context, /*VolumeType.STANDARD*/ 'standard', {
        noDevice.interpolated || virtualName.interpolated
      })
      this.@volumeSize = from.volumeSize.interpolateValue(context, (Long)null, {
        noDevice.interpolated || virtualName.interpolated
      }) { Long interpolated ->
        interpolated > 0 ? interpolated : null
      }
      this.@kmsKeyId = from.kmsKeyId.interpolateValue(context, (String)null, {
        noDevice.interpolated || virtualName.interpolated
      })
    }

    @Override
    BlockDevice interpolate(Context context) {
      return (BlockDevice)new BlockDeviceImpl(context, this)
    }
  }

  /*enum VolumeType {
    STANDARD,
    IO1,
    GP2,
    SC1,
    ST1

    @JsonValue
    @Override
    String toString() {
      this.name().toLowerCase()
    }
  }

  interface InterpolableVolumeType extends InterpolableEnum<VolumeType, InterpolableVolumeType> {
    @InheritConstructors
    final class ImmutableRaw extends InterpolableEnum.ImmutableRaw<VolumeType, InterpolableVolumeType, Interpolated, AlreadyInterpolated> implements InterpolableVolumeType { }

    @InheritConstructors
    final class Raw extends InterpolableEnum.Raw<VolumeType, InterpolableVolumeType, Interpolated, AlreadyInterpolated> implements InterpolableVolumeType { }

    @InheritConstructors
    final class Interpolated extends InterpolableEnum.Interpolated<VolumeType, InterpolableVolumeType, AlreadyInterpolated> implements InterpolableVolumeType { }

    @InheritConstructors
    final class AlreadyInterpolated extends InterpolableEnum.AlreadyInterpolated<VolumeType, InterpolableVolumeType> implements InterpolableVolumeType { }
  }*/
}
