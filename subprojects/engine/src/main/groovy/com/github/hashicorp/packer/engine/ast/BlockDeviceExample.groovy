package com.github.hashicorp.packer.engine.ast


import com.github.hashicorp.packer.engine.annotations.ComputedInput
import com.github.hashicorp.packer.engine.types.InterpolableBoolean
import com.github.hashicorp.packer.engine.types.InterpolableLong
import com.github.hashicorp.packer.engine.types.InterpolableObject
import com.github.hashicorp.packer.engine.types.InterpolableString
import com.github.hashicorp.packer.template.Context
import groovy.transform.CompileStatic
import org.gradle.api.tasks.Internal
import org.gradle.api.tasks.Optional

@CompileStatic
// @Builder(builderStrategy = ExternalStrategy, forClass = BlockDevice)
// @JsonDeserialize(builder = BlockDeviceBuilder) // TOTEST
interface BlockDeviceExample extends InterpolableObject<BlockDeviceExample> {
//  @Internal // TOTEST
//  private InterpolableBoolean deleteOnTermination
//
//  @ComputedInput
//  @Synchronized
//  InterpolableBoolean getDeleteOnTermination() {
//    if (InterpolableBoolean.Utils.requiresInitialization(deleteOnTermination)) {
//      deleteOnTermination = InterpolableBoolean.Utils.initWithDefault(deleteOnTermination, Boolean.FALSE, { noDevice.get() || virtualName.get() } )
//    }
//    deleteOnTermination
//  }
//
//  @Synchronized
//  void setDeleteOnTermination(final InterpolableBoolean deleteOnTermination) {
//    if (readOnly) {
//      throw new ReadOnlyPropertyException('deleteOnTermination', this.class.canonicalName)
//    }
//    this.@deleteOnTermination = deleteOnTermination
//  }
//
//  @Internal
//  InterpolableString deviceName
//
//  @ComputedInput
//  @Synchronized
//  InterpolableString getDeviceName() {
//    if (InterpolableString.Utils.requiresInitialization(deviceName)) {
//      deviceName = InterpolableString.Utils.initWithDefault(deviceName, (String)null)
//    }
//    deviceName
//  }
//
//  @Synchronized
//  void setDeviceName(final InterpolableString deviceName) {
//    if (readOnly) {
//      throw new ReadOnlyPropertyException('deviceName', this.class.canonicalName)
//    }
//    this.@deviceName = deviceName
//  }
//
//  @Internal
//  private InterpolableBoolean encrypted
//
//  @ComputedInput
//  @Synchronized
//  InterpolableBoolean getEncrypted() {
//    if (InterpolableBoolean.Utils.requiresInitialization(encrypted)) {
//      encrypted = InterpolableBoolean.Utils.initWithDefault(encrypted, Boolean.FALSE, { noDevice.get() || virtualName.get() || snapshotId.get() } )
//    }
//    encrypted
//  }
//
//  @Synchronized
//  void setEncrypted(final InterpolableBoolean encrypted) {
//    if (readOnly) {
//      throw new ReadOnlyPropertyException('encrypted', this.class.canonicalName)
//    }
//    this.@encrypted = encrypted
//  }
//
//  @Internal
//  private InterpolableLong iops
//
//  @ComputedInput
//  @Synchronized
//  // @Default(default = { volumeType.get() != 'io1' /* TODO: Bug in either packer or AWS documentation. This should be supported for gp2 volumes too */ })
//  InterpolableLong getIops() {
//    if (InterpolableLong.Utils.requiresInitialization(iops)) {
//      iops = InterpolableLong.Utils.initWithDefault(iops, (Long)null, { volumeType.get() != 'io1' /* TODO: Bug in either packer or AWS documentation. This should be supported for gp2 volumes too */ } )
//    }
//    iops
//  }
//
//  @Synchronized
//  void setIops(final InterpolableLong iops) {
//    if (readOnly) {
//      throw new ReadOnlyPropertyException('iops', this.class.canonicalName)
//    }
//    this.@iops = iops
//  }
//
//  @Internal
//  private InterpolableBoolean noDevice
//
//  @ComputedInput
//  @Synchronized
//  InterpolableBoolean getNoDevice() {
//    if (InterpolableBoolean.Utils.requiresInitialization(noDevice)) {
//      noDevice = InterpolableBoolean.Utils.initWithDefault(noDevice, Boolean.FALSE)
//    }
//    noDevice
//  }
//
//  @Synchronized
//  void setDeleteOnTermination(final InterpolableBoolean deleteOnTermination) {
//    if (readOnly) {
//      throw new ReadOnlyPropertyException('deleteOnTermination', this.class.canonicalName)
//    }
//    this.@deleteOnTermination = deleteOnTermination
//  }
//
//  @Internal
//  private InterpolableString snapshotId
//
//  @ComputedInput
//  @Synchronized
//  InterpolableString getSnapshotId() {
//    if (InterpolableString.Utils.requiresInitialization(snapshotId)) {
//      snapshotId = InterpolableString.Utils.initWithDefault(snapshotId, (String)null, { noDevice.get() || virtualName.get() }, { String interpolatedValue -> interpolatedValue.empty ? null : interpolatedValue } )
//    }
//    snapshotId
//  }
//
//  @Synchronized
//  void setDeleteOnTermination(final InterpolableBoolean deleteOnTermination) {
//    if (readOnly) {
//      throw new ReadOnlyPropertyException('deleteOnTermination', this.class.canonicalName)
//    }
//    this.@deleteOnTermination = deleteOnTermination
//  }
//
//  @Internal
//  private InterpolableString virtualName
//
//  @ComputedInput
//  @Synchronized
//  InterpolableString getVirtualName() {
//    if (InterpolableString.Utils.requiresInitialization(virtualName)) {
//      virtualName = InterpolableString.Utils.initWithDefault(virtualName, (String)null, { noDevice.get() }, { String interpolatedValue -> interpolatedValue.startsWith('ephemeral') ? null : interpolatedValue } )
//    }
//    virtualName
//  }
//
//  @Synchronized
//  void setDeleteOnTermination(final InterpolableBoolean deleteOnTermination) {
//    if (readOnly) {
//      throw new ReadOnlyPropertyException('deleteOnTermination', this.class.canonicalName)
//    }
//    this.@deleteOnTermination = deleteOnTermination
//  }
//
//  @Internal
//  private InterpolableString volumeType
//
//  @ComputedInput
//  @Synchronized
//  InterpolableString getVolumeType() {
//    if (InterpolableString.Utils.requiresInitialization(volumeType)) {
//      volumeType = InterpolableString.Utils.initWithDefault(volumeType, 'standard', { noDevice.get() || virtualName.get() } )
//    }
//    volumeType
//  }
//
//  @Synchronized
//  void setDeleteOnTermination(final InterpolableBoolean deleteOnTermination) {
//    if (readOnly) {
//      throw new ReadOnlyPropertyException('deleteOnTermination', this.class.canonicalName)
//    }
//    this.@deleteOnTermination = deleteOnTermination
//  }
//
//  @Internal
//  private InterpolableLong volumeSize // TODO: If you're creating the volume from a snapshot and don't specify a volume size, the default is the snapshot size.
//
//  @ComputedInput
//  @Synchronized
//  InterpolableLong getVolumeSize() {
//    if (InterpolableLong.Utils.requiresInitialization(volumeSize)) {
//      volumeSize = InterpolableLong.Utils.initWithDefault(volumeSize, (Long)null, { noDevice.get() || virtualName.get() }, { Long interpolableValue -> interpolableValue > 0 ? interpolableValue : null} )
//    }
//    volumeSize
//  }
//
//  @Synchronized
//  void setDeleteOnTermination(final InterpolableBoolean deleteOnTermination) {
//    if (readOnly) {
//      throw new ReadOnlyPropertyException('deleteOnTermination', this.class.canonicalName)
//    }
//    this.@deleteOnTermination = deleteOnTermination
//  }
//
//  @Optional
//  private InterpolableString kmsKeyId
//
//  // TODO: get, set
//
//  BlockDeviceExample() {
//    this(false)
//  }
//
//  private BlockDeviceExample(boolean readOnly) {
//    super(readOnly)
//  }
//
//  @Override
//  protected BlockDevice getAsReadOnly() {
//    BlockDevice result = new BlockDevice(true)
//    result.@deleteOnTermination = this.@deleteOnTermination.asReadOnly()
//    result.@deviceName = this.@deviceName.asReadOnly()
//    result.@encrypted = this.@encrypted.asReadOnly()
//    result.@iops = this.@iops.asReadOnly()
//    result.@noDevice = this.@noDevice.asReadOnly()
//    result.@snapshotId = this.@snapshotId.asReadOnly()
//    result.@virtualName = this.@virtualName.asReadOnly()
//    result.@volumeType = this.@volumeType.asReadOnly()
//    result.@volumeSize = this.@volumeSize.asReadOnly()
//    result.@kmsKeyId = this.@kmsKeyId.asReadOnly()
//    result
//  }
//
//  @Synchronized
//  @Override
//  BlockDevice interpolate(Context context) {
//    BlockDevice result = new BlockDevice(true)
//    result.@deleteOnTermination = deleteOnTermination.interpolateValue(context, result)
//    result.@deviceName = deviceName.interpolateValue(context, result)
//    result.@encrypted = encrypted.interpolateValue(context, result)
//    result.@iops = iops.interpolateValue(context, result)
//    result.@noDevice = noDevice.interpolateValue(context, result)
//    result.@snapshotId = snapshotId.interpolateValue(context, result)
//    result.@virtualName = virtualName.interpolateValue(context, result)
//    result.@volumeType = volumeType.interpolateValue(context, result)
//    result.@volumeSize = volumeSize.interpolateValue(context, result)
//    result.@kmsKeyId = kmsKeyId.interpolateValue(context, result)
//    result
//  }
}
