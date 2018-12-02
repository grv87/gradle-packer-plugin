package com.github.hashicorp.packer.engine.ast

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonGetter
import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonProperty
import com.github.hashicorp.packer.engine.annotations.Default
import com.github.hashicorp.packer.engine.annotations.IgnoreIf
import com.github.hashicorp.packer.engine.annotations.PostProcess
import com.github.hashicorp.packer.engine.types.InterpolableBoolean
import com.github.hashicorp.packer.engine.types.InterpolableLong
import com.github.hashicorp.packer.engine.types.InterpolableObject
import com.github.hashicorp.packer.engine.types.InterpolableString
import com.github.hashicorp.packer.template.Context
import groovy.transform.CompileStatic
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.Internal
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
  @IgnoreIf({ volumeType.interpolated != 'io1' /* TODO: Bug in either Packer or AWS documentation. This should be supported for gp2 volumes too */ })
  abstract InterpolableLong getIops()

  @Input
  @Default({ Boolean.FALSE })
  abstract InterpolableBoolean getNoDevice()

  @Input
  @IgnoreIf({ noDevice.interpolated || virtualName.interpolated })
  abstract InterpolableString getSnapshotId()

  @Input
  @IgnoreIf({ noDevice.interpolated })
  @PostProcess({ String interpolatedValue -> interpolatedValue.startsWith('ephemeral') ? null : interpolatedValue })
  abstract InterpolableString getVirtualName()

  @Input
  @Default({ 'standard' })
  @IgnoreIf({ noDevice.interpolated || virtualName.interpolated })
  abstract InterpolableString getVolumeType()

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
    @JsonIgnore
    @Override
    InterpolableBoolean getDeleteOnTermination() {
      this.@deleteOnTermination
    }

    @Internal
    @JsonGetter('delete_on_termination')
    protected InterpolableBoolean getDeleteOnTerminationForJson() {
      this.@deleteOnTermination.raw != null ? this.@deleteOnTermination : null
    }

    private final InterpolableString deviceName

    @Input
    @JsonIgnore
    @Override
    InterpolableString getDeviceName() {
      this.@deviceName
    }

    @Internal
    @JsonGetter('device_name')
    protected InterpolableString getDeviceNameForJson() {
      this.@deviceName.raw != null ? this.@deviceName : null
    }

    private final InterpolableBoolean encrypted

    @Input
    @Optional
    @JsonIgnore
    @Override
    InterpolableBoolean getEncrypted() {
      this.@encrypted
    }

    @Internal
    @JsonGetter('encrypted')
    protected InterpolableBoolean getEncryptedForJson() {
      this.@encrypted.raw != null ? this.@encrypted : null
    }

    private final InterpolableLong iops

    @Input
    @Optional
    @JsonIgnore
    @Override
    InterpolableLong getIops() {
      this.@iops
    }

    @Internal
    @JsonGetter('iops')
    protected InterpolableLong getIopsForJson() {
      this.@iops.raw != null ? this.@iops : null
    }

    private final InterpolableBoolean noDevice

    @Input
    @Optional
    @JsonIgnore
    @Override
    InterpolableBoolean getNoDevice() {
      this.@noDevice
    }

    @Internal
    @JsonGetter('no_device')
    protected InterpolableBoolean getNoDeviceForJson() {
      this.@noDevice.raw != null ? this.@noDevice : null
    }

    private final InterpolableString snapshotId

    @Input
    @Optional
    @JsonIgnore
    @Override
    InterpolableString getSnapshotId() {
      this.@snapshotId
    }

    @Internal
    @JsonGetter('snapshot_id')
    protected InterpolableString getSnapshotIdForJson() {
      this.@snapshotId.raw != null ? this.@snapshotId : null
    }

    private final InterpolableString virtualName

    @Input
    @Optional
    @JsonIgnore
    @Override
    InterpolableString getVirtualName() {
      this.@virtualName
    }

    @Internal
    @JsonGetter('virtual_name')
    protected InterpolableString getVirtualNameForJson() {
      this.@virtualName.raw != null ? this.@virtualName : null
    }

    private final InterpolableString volumeType // TODO: Enum

    @Input
    @Optional
    @JsonIgnore
    @Override
    InterpolableString getVolumeType() {
      this.@volumeType
    }

    @Internal
    @JsonGetter('volume_type')
    protected InterpolableString getVolumeTypeForJson() {
      this.@volumeType.raw != null ? this.@volumeType : null
    }

    private final InterpolableLong volumeSize

    @Input
    @Optional
    @JsonIgnore
    @Override
    InterpolableLong getVolumeSize() {
      this.@volumeSize
    }

    @Internal
    @JsonGetter('delete-on-termination')
    protected InterpolableBoolean getVolumeSizeForJson() {
      this.@deleteOnTermination.raw != null ? this.@deleteOnTermination : null
    }

    private final InterpolableString kmsKeyId

    @Input
    @Optional
    @JsonIgnore
    @Override
    InterpolableString getKmsKeyId() {
      this.@kmsKeyId
    }

    @Internal
    @JsonGetter('kms_key_id')
    protected InterpolableString getKmsKeyIdForJson() {
      this.@kmsKeyId.raw != null ? this.@kmsKeyId : null
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
      @JsonProperty('volume_type') InterpolableString volumeType,
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
      this.@volumeType = volumeType ?: new InterpolableString.ImmutableRaw()
      this.@volumeSize = volumeSize ?: new InterpolableLong.ImmutableRaw()
      this.@kmsKeyId = kmsKeyId ?: new InterpolableString.ImmutableRaw()
    }

    private BlockDeviceImpl(Context context, BlockDeviceResult interpolateFrom) {
      this.@deleteOnTermination = interpolateFrom.deleteOnTermination.interpolateValue(context, Boolean.FALSE, {
        noDevice.interpolated || virtualName.interpolated
      })
      this.@deviceName = interpolateFrom.deviceName.interpolateValue(context, (String)null)
      this.@encrypted = interpolateFrom.encrypted.interpolateValue(context, Boolean.FALSE, {
        noDevice.interpolated || virtualName.interpolated || snapshotId.interpolated
      })
      this.@iops = interpolateFrom.iops.interpolateValue(context, (Long)null, {
        /* TODO: Bug in either packer or AWS documentation. This should be supported for gp2 volumes too */
        volumeType.interpolated != 'io1'
      })
      this.@noDevice = interpolateFrom.noDevice.interpolateValue(context, Boolean.FALSE)
      this.@snapshotId = interpolateFrom.snapshotId.interpolateValue(context, (String)null, {
        noDevice.interpolated || virtualName.interpolated
      })
      this.@virtualName = interpolateFrom.virtualName.interpolateValue(context, (String)null, {
        noDevice.interpolated
      }, { String interpolated -> interpolated.startsWith('ephemeral') ? null : interpolated })
      this.@volumeType = interpolateFrom.volumeType.interpolateValue(context, 'standard', {
        noDevice.interpolated || virtualName.interpolated
      })
      this.@volumeSize = interpolateFrom.volumeSize.interpolateValue(context, (Long)null, {
        noDevice.interpolated || virtualName.interpolated
      }, { Long interpolated -> interpolated > 0 ? interpolated : null })
      this.@kmsKeyId = interpolateFrom.kmsKeyId.interpolateValue(context, (String)null, {
        noDevice.interpolated || virtualName.interpolated
      })
    }

    @Override
    BlockDeviceResult interpolate(Context context) {
      new BlockDeviceImpl(context, this)
    }
  }
}
