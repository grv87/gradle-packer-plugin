package com.github.hashicorp.packer.engine.ast

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.annotation.JsonValue
import com.github.hashicorp.packer.engine.annotations.Default
import com.github.hashicorp.packer.engine.annotations.IgnoreIf
import com.github.hashicorp.packer.engine.annotations.PostProcess
import com.github.hashicorp.packer.engine.types.InterpolableBoolean
import com.github.hashicorp.packer.engine.types.InterpolableEnum
import com.github.hashicorp.packer.engine.types.InterpolableLong
import com.github.hashicorp.packer.engine.types.InterpolableObject
import com.github.hashicorp.packer.engine.types.InterpolableString
import com.github.hashicorp.packer.template.Context
import groovy.transform.CompileStatic
import groovy.transform.InheritConstructors
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.Optional
import org.gradle.internal.impldep.com.fasterxml.jackson.databind.annotation.JsonDeserialize

@JsonDeserialize(as = BlockDeviceImpl)
@CompileStatic
interface BlockDeviceResult extends InterpolableObject<BlockDeviceResult> {
  @Input
  @Default({ Boolean.FALSE })
  @IgnoreIf({ noDevice.interpolated || virtualName.interpolated })
  abstract InterpolableBoolean getDeleteOnTermination()

  @Input
  abstract InterpolableString getDeviceName()

  @Input
  @Default({ Boolean.FALSE })
  @IgnoreIf({ noDevice.interpolated || virtualName.interpolated || snapshotId.interpolated })
  abstract InterpolableBoolean getEncrypted()

  @Input
  @IgnoreIf({ volumeType.interpolated != VolumeType.IO1 /* TODO: Bug in either Packer or AWS documentation. This should be supported for gp2 volumes too */ })
  abstract InterpolableLong getIops()

  @Input
  @Default({ Boolean.FALSE })
  abstract InterpolableBoolean getNoDevice()

  @Input
  @IgnoreIf({ noDevice.interpolated || virtualName.interpolated })
  abstract InterpolableString getSnapshotId()

  @Input
  @IgnoreIf({ noDevice.interpolated })
  @PostProcess({ String interpolated -> interpolated.startsWith('ephemeral') ? null : interpolated })
  abstract InterpolableString getVirtualName()

  @Input
  @Default({ 'standard' })
  @IgnoreIf({ noDevice.interpolated || virtualName.interpolated })
  abstract InterpolableVolumeType getVolumeType()

  @Input
  // @Default() TODO: If you're creating the volume from a snapshot and don't specify a volume size, the default is the snapshot size.
  @IgnoreIf({ noDevice.interpolated || virtualName.interpolated })
  @PostProcess({ Long interpolableValue -> interpolableValue > 0 ? interpolableValue : null})
  abstract InterpolableLong getVolumeSize()

  @Input
  @IgnoreIf({ noDevice.interpolated || virtualName.interpolated })
  abstract InterpolableString getKmsKeyId()

  static final class BlockDeviceImpl implements BlockDeviceResult {
    private final InterpolableBoolean deleteOnTermination

    @Input
    @Optional
    @Override
    InterpolableBoolean getDeleteOnTermination() {
      this.@deleteOnTermination
    }

    private final InterpolableString deviceName

    @Input
    @Override
    InterpolableString getDeviceName() {
      this.@deviceName
    }

    private final InterpolableBoolean encrypted

    @Input
    @Optional
    @Override
    InterpolableBoolean getEncrypted() {
      this.@encrypted
    }

    private final InterpolableLong iops

    @Input
    @Optional
    @Override
    InterpolableLong getIops() {
      this.@iops
    }

    private final InterpolableBoolean noDevice

    @Input
    @Optional
    @Override
    InterpolableBoolean getNoDevice() {
      this.@noDevice
    }

    private final InterpolableString snapshotId

    @Input
    @Optional
    @Override
    InterpolableString getSnapshotId() {
      this.@snapshotId
    }

    private final InterpolableString virtualName

    @Input
    @Optional
    @Override
    InterpolableString getVirtualName() {
      this.@virtualName
    }

    private final InterpolableVolumeType volumeType

    @Input
    @Optional
    @Override
    InterpolableVolumeType getVolumeType() {
      this.@volumeType
    }

    private final InterpolableLong volumeSize

    @Input
    @Optional
    @Override
    InterpolableLong getVolumeSize() {
      this.@volumeSize
    }

    private final InterpolableString kmsKeyId

    @Input
    @Optional
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
      @JsonProperty('on_devide') InterpolableBoolean noDevice,
      @JsonProperty('snapshot_id') InterpolableString snapshotId,
      @JsonProperty('virtual_name') InterpolableString virtualName,
      @JsonProperty('volume_type') InterpolableVolumeType volumeType,
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
      this.@volumeType = volumeType ?: new InterpolableVolumeType.ImmutableRaw()
      this.@volumeSize = volumeSize ?: new InterpolableLong.ImmutableRaw()
      this.@kmsKeyId = kmsKeyId ?: new InterpolableString.ImmutableRaw()
    }

    private BlockDeviceImpl(Context context, BlockDeviceResult raw) {
      this.@deleteOnTermination = raw.deleteOnTermination.interpolateValue(context, Boolean.FALSE, {
        noDevice.interpolated || virtualName.interpolated
      })
      this.@deviceName = raw.deviceName.interpolateValue(context, (String)null)
      this.@encrypted = raw.encrypted.interpolateValue(context, Boolean.FALSE, {
        noDevice.interpolated || virtualName.interpolated || snapshotId.interpolated
      })
      this.@iops = raw.iops.interpolateValue(context, (Long)null, {
        /* TODO: Bug in either packer or AWS documentation. This should be supported for gp2 volumes too */
        volumeType.interpolated != VolumeType.IO1
      })
      this.@noDevice = raw.noDevice.interpolateValue(context, Boolean.FALSE)
      this.@snapshotId = raw.snapshotId.interpolateValue(context, (String)null, {
        noDevice.interpolated || virtualName.interpolated
      })
      this.@virtualName = raw.virtualName.interpolateValue(context, (String)null, {
        noDevice.interpolated
      }, { String interpolated -> interpolated.startsWith('ephemeral') ? null : interpolated })
      this.@volumeType = raw.volumeType.interpolateValue(context, VolumeType.STANDARD, {
        noDevice.interpolated || virtualName.interpolated
      })
      this.@volumeSize = raw.volumeSize.interpolateValue(context, (Long)null, {
        noDevice.interpolated || virtualName.interpolated
      }, { Long interpolated -> interpolated > 0 ? interpolated : null })
      this.@kmsKeyId = raw.kmsKeyId.interpolateValue(context, (String)null, {
        noDevice.interpolated || virtualName.interpolated
      })
    }

    @Override
    BlockDeviceResult interpolate(Context context) {
      new BlockDeviceImpl(context, this)
    }
  }

  enum VolumeType {
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
    final class ImmutableRaw extends InterpolableEnum.ImmutableRaw<VolumeType, InterpolableVolumeType> implements InterpolableVolumeType { }

    @InheritConstructors
    final class Raw extends InterpolableEnum.ImmutableRaw<VolumeType, InterpolableVolumeType> implements InterpolableVolumeType { }

    @InheritConstructors
    final class Interpolated extends InterpolableEnum.ImmutableRaw<VolumeType, InterpolableVolumeType> implements InterpolableVolumeType { }

    @InheritConstructors
    final class AlreadyInterpolated extends InterpolableEnum.ImmutableRaw<VolumeType, InterpolableVolumeType> implements InterpolableVolumeType { }
  }
}
