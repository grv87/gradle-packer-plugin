package com.github.hashicorp.packer.engine.ast

import com.fasterxml.jackson.annotation.JsonIgnore
import com.github.hashicorp.packer.engine.annotations.Default
import com.github.hashicorp.packer.engine.annotations.IgnoreIf
import com.github.hashicorp.packer.engine.annotations.PostProcess
import com.github.hashicorp.packer.engine.types.InterpolableBoolean
import com.github.hashicorp.packer.engine.types.InterpolableLong
import com.github.hashicorp.packer.engine.types.InterpolableObject
import com.github.hashicorp.packer.engine.types.InterpolableString
import com.github.hashicorp.packer.template.Context
import groovy.transform.CompileStatic
import groovy.transform.Synchronized
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.Internal
import org.gradle.api.tasks.Optional
import org.gradle.internal.impldep.com.fasterxml.jackson.databind.annotation.JsonDeserialize

@JsonDeserialize(as = BlockDeviceImpl)
@CompileStatic
abstract class BlockDeviceResult implements InterpolableObject<BlockDeviceResult> {
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

  static final class BlockDeviceImpl extends BlockDeviceResult {
    @Internal // TOTEST
    protected InterpolableBoolean deleteOnTermination

    @Input
    @Optional
    @JsonIgnore
    @Synchronized
    @Override
    InterpolableBoolean getDeleteOnTermination() {
      if (InterpolableBoolean.Utils.requiresInitialization(deleteOnTermination)) {
        deleteOnTermination = InterpolableBoolean.Utils.initWithDefault(deleteOnTermination, Boolean.FALSE, {
          noDevice.get() || virtualName.get()
        })
      }
      deleteOnTermination
    }

    protected InterpolableString deviceName

    @Input
    @JsonIgnore
    @Synchronized
    @Override
    InterpolableString getDeviceName() {
      if (InterpolableString.Utils.requiresInitialization(deviceName)) {
        deviceName = InterpolableString.Utils.initWithDefault(deviceName, (String) null)
      }
      deviceName
    }

    @Internal
    protected InterpolableBoolean encrypted

    @Input
    @Optional
    @JsonIgnore
    @Synchronized
    @Override
    InterpolableBoolean getEncrypted() {
      if (InterpolableBoolean.Utils.requiresInitialization(encrypted)) {
        encrypted = InterpolableBoolean.Utils.initWithDefault(encrypted, Boolean.FALSE, {
          noDevice.get() || virtualName.get() || snapshotId.get()
        })
      }
      encrypted
    }

    protected InterpolableLong iops

    @Input
    @Optional
    @JsonIgnore
    @Synchronized
    @Override
    InterpolableLong getIops() {
      if (InterpolableLong.Utils.requiresInitialization(iops)) {
        iops = InterpolableLong.Utils.initWithDefault(iops, (Long) null, {
          volumeType.get() != 'io1'
          /* TODO: Bug in either packer or AWS documentation. This should be supported for gp2 volumes too */
        })
      }
      iops
    }

    protected InterpolableBoolean noDevice

    @Input
    @Optional
    @JsonIgnore
    @Synchronized
    @Override
    InterpolableBoolean getNoDevice() {
      if (InterpolableBoolean.Utils.requiresInitialization(noDevice)) {
        noDevice = InterpolableBoolean.Utils.initWithDefault(noDevice, Boolean.FALSE)
      }
      noDevice
    }

    protected InterpolableString snapshotId

    @Input
    @Optional
    @JsonIgnore
    @Synchronized
    @Override
    InterpolableString getSnapshotId() {
      if (InterpolableString.Utils.requiresInitialization(snapshotId)) {
        snapshotId = InterpolableString.Utils.initWithDefault(snapshotId, (String) null, {
          noDevice.get() || virtualName.get()
        })
      }
      snapshotId
    }

    @Internal
    protected InterpolableString virtualName

    @Input
    @Optional
    @JsonIgnore
    @Synchronized
    @Override
    InterpolableString getVirtualName() {
      if (InterpolableString.Utils.requiresInitialization(virtualName)) {
        virtualName = InterpolableString.Utils.initWithDefault(virtualName, (String) null, {
          noDevice.get()
        }, { String interpolatedValue -> interpolatedValue.startsWith('ephemeral') ? null : interpolatedValue })
      }
      virtualName
    }

    @Internal
    protected InterpolableString volumeType

    @Input
    @Optional
    @JsonIgnore
    @Synchronized
    @Override
    InterpolableString getVolumeType() {
      if (InterpolableString.Utils.requiresInitialization(volumeType)) {
        volumeType = InterpolableString.Utils.initWithDefault(volumeType, 'standard', {
          noDevice.get() || virtualName.get()
        })
      }
      volumeType
    }

    protected InterpolableLong volumeSize

    @Input
    @Optional
    @JsonIgnore
    @Synchronized
    @Override
    InterpolableLong getVolumeSize() {
      if (InterpolableLong.Utils.requiresInitialization(volumeSize)) {
        volumeSize = InterpolableLong.Utils.initWithDefault(volumeSize, (Long) null, {
          noDevice.get() || virtualName.get()
        }, { Long interpolableValue -> interpolableValue > 0 ? interpolableValue : null })
      }
      volumeSize
    }

    protected InterpolableString kmsKeyId

    @Input
    @Optional
    @JsonIgnore
    @Synchronized
    @Override
    InterpolableString getKmsKeyId() {
      if (InterpolableString.Utils.requiresInitialization(kmsKeyId)) {
        kmsKeyId = InterpolableString.Utils.initWithDefault(kmsKeyId, (String) null, {
          noDevice.get() || virtualName.get()
        })
      }
      kmsKeyId
    }

    @Override
    BlockDeviceResult interpolate(Context context) {
      BlockDeviceImpl result = new BlockDeviceImpl()
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
}
