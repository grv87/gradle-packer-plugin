package com.github.hashicorp.packer.builder.amazon.common

import com.fasterxml.jackson.databind.annotation.JsonDeserialize
import com.github.hashicorp.packer.engine.annotations.ComputedInput
import com.github.hashicorp.packer.engine.types.InterpolableBoolean
import com.github.hashicorp.packer.engine.types.InterpolableLong
import com.github.hashicorp.packer.engine.types.InterpolableObject
import com.github.hashicorp.packer.engine.types.InterpolableString
import groovy.transform.AutoClone
import groovy.transform.AutoCloneStyle
import groovy.transform.CompileStatic
import groovy.transform.builder.Builder
import groovy.transform.builder.ExternalStrategy
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.Internal
import org.gradle.api.tasks.Optional

@AutoClone(style = AutoCloneStyle.SIMPLE)
@CompileStatic
// @Builder(builderStrategy = ExternalStrategy, forClass = BlockDevice)
// @JsonDeserialize(builder = BlockDeviceBuilder) // TOTEST
final class BlockDevice extends InterpolableObject {
  private final $LOCK = new Object()

  @Internal
  private volatile InterpolableBoolean deleteOnTermination

  @SuppressWarnings("GroovyResultOfAssignmentUsed")
  @ComputedInput
  // @Optional // TOTEST: Should not be required, since it is not null, just serialized to null
  InterpolableBoolean getDeleteOnTermination() {
    InterpolableBoolean deleteOnTerminationLocal = deleteOnTermination
    if (deleteOnTerminationLocal != null && deleteOnTerminationLocal.initialized) {
      deleteOnTerminationLocal
    }
    synchronized($LOCK) {
      if (deleteOnTermination == null || !deleteOnTermination.initialized) {
        deleteOnTermination = InterpolableBoolean.initWithDefault(deleteOnTermination, false, { !noDevice.get() && !virtualName.get() } )
      }
      deleteOnTermination
    }
  }

  @Internal
  InterpolableString deviceName

  @ComputedInput
  String getActualDeviceName() {
    deviceName.interpolatedValue
  }

  @Internal
  InterpolableBoolean encrypted = InterpolableBoolean.withDefault(false)

  @ComputedInput
  @Optional
  Boolean getActualEncrypted() {
    !actualNoDevice && snapshotId?.interpolatedValue == null ? encrypted.interpolatedValue : null
  }

  @Internal
  InterpolableLong iops

  @ComputedInput
  @Optional
  Long getActualIops() {
    actualVolumeType == 'io1' ? iops?.interpolatedValue : null
  }

  @Internal
  protected InterpolableBoolean noDevice

  @SuppressWarnings("GroovyResultOfAssignmentUsed")
  @ComputedInput
  @Optional
  InterpolableBoolean getNoDevice() {
    noDevice = InterpolableBoolean.initWithDefault(noDevice, false)
  }

  InterpolableString snapshotId

  @Internal
  InterpolableString virtualName

  @ComputedInput
  @Optional
  String getActualVirtualName() {
    !actualNoDevice ? virtualName?.interpolatedValue : null
  }

  @Internal
  InterpolableString volumeType = InterpolableString.withDefault('standard') // TODO: enum gp2, io1, st1, sc1, or standard

  @ComputedInput
  @Optional
  String getActualVolumeType() {
    !noDevice.interpolatedValue ? volumeType.interpolatedValue : null
  }

  @Input
  @Optional
  InterpolableLong volumeSize // If you're creating the volume from a snapshot and don't specify a volume size, the default is the snapshot size.

  @Optional
  InterpolableString kmsKeyId

  @Override
  protected void doInterpolate() {
    deleteOnTermination = InterpolableBoolean.withDefault(context, false, { !noDevice.get() && !virtualName.get()})

    deleteOnTermination.interpolate context

    deleteOnTermination.interpolate context
    deviceName.interpolate context
    encrypted.interpolate context
    iops.interpolate context
    noDevice.interpolate context
    snapshotId.interpolate context
    virtualName.interpolate context
    volumeType.interpolate context
    volumeSize.interpolate context
    kmsKeyId.interpolate context
  }
}
