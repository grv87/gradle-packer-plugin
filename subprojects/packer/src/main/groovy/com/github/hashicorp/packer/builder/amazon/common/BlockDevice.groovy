package com.github.hashicorp.packer.builder.amazon.common

import com.fasterxml.jackson.annotation.JsonSetter
import com.github.hashicorp.packer.engine.annotations.ComputedInput
import com.github.hashicorp.packer.engine.types.InterpolableBoolean
import com.github.hashicorp.packer.engine.types.InterpolableLong
import com.github.hashicorp.packer.engine.types.InterpolableObject
import com.github.hashicorp.packer.engine.types.InterpolableString
import com.github.hashicorp.packer.template.Context
import groovy.transform.CompileStatic
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.Internal
import org.gradle.api.tasks.Optional

@CompileStatic
// @Builder(builderStrategy = ExternalStrategy, forClass = BlockDevice)
// @JsonDeserialize(builder = BlockDeviceBuilder) // TOTEST
final class BlockDevice implements InterpolableObject<BlockDevice> {
  @Internal // TOTEST
  private InterpolableBoolean deleteOnTermination

  @ComputedInput
  InterpolableBoolean getDeleteOnTermination() {
    if (InterpolableBoolean.Utils.requiresInitialization(deleteOnTermination)) {
      deleteOnTermination = InterpolableBoolean.Utils.initWithDefault(deleteOnTermination, Boolean.FALSE, { noDevice.get() || virtualName.get() } )
    }
    deleteOnTermination
  }

  @Internal
  InterpolableString deviceName

  @ComputedInput
  InterpolableString getDeviceName() {
    if (InterpolableString.Utils.requiresInitialization(deviceName)) {
      deviceName = InterpolableString.Utils.initWithDefault(deviceName, (String)null)
    }
    deviceName
  }

  @Internal
  private InterpolableBoolean encrypted

  @ComputedInput
  InterpolableBoolean getEncrypted() {
    if (InterpolableBoolean.Utils.requiresInitialization(encrypted)) {
      encrypted = InterpolableBoolean.Utils.initWithDefault(encrypted, Boolean.FALSE, { noDevice.get() || virtualName.get() || snapshotId.get() } )
    }
    encrypted
  }

  @Internal
  private InterpolableLong iops

  @ComputedInput
  // @Default(default = { volumeType.get() != 'io1' /* TODO: Bug in either packer or AWS documentation. This should be supported for gp2 volumes too */ })
  InterpolableLong getIops() {
    if (InterpolableLong.Utils.requiresInitialization(iops)) {
      iops = InterpolableLong.Utils.initWithDefault(iops, (Long)null, { volumeType.get() != 'io1' /* TODO: Bug in either packer or AWS documentation. This should be supported for gp2 volumes too */ } )
    }
    iops
  }

  @Internal
  private InterpolableBoolean noDevice

  @ComputedInput
  InterpolableBoolean getNoDevice() {
    if (InterpolableBoolean.Utils.requiresInitialization(noDevice)) {
      noDevice = InterpolableBoolean.Utils.initWithDefault(noDevice, Boolean.FALSE)
    }
    noDevice
  }

  @Internal
  private InterpolableString snapshotId

  @ComputedInput
  InterpolableString getSnapshotId() {
    if (InterpolableString.Utils.requiresInitialization(snapshotId)) {
      snapshotId = InterpolableString.Utils.initWithDefault(snapshotId, (String)null, { noDevice.get() || virtualName.get() }/*, { String interpolatedValue -> (interpolatedValue ?: null) }*/ )
    }
    snapshotId
  }

  @Internal
  private InterpolableString virtualName

  @ComputedInput
  InterpolableString getVirtualName() {
    if (InterpolableString.Utils.requiresInitialization(virtualName)) {
      virtualName = InterpolableString.Utils.initWithDefault(virtualName, (String)null, { noDevice.get() }, { String interpolatedValue -> interpolatedValue.startsWith('ephemeral') ? null : interpolatedValue } )
    }
    virtualName
  }

  @Internal
  private InterpolableString volumeType

  @ComputedInput
  InterpolableString getVolumeType() {
    if (InterpolableString.Utils.requiresInitialization(volumeType)) {
      volumeType = InterpolableString.Utils.initWithDefault(volumeType, 'standard', { noDevice.get() || virtualName.get() } )
    }
    volumeType
  }

  @Internal
  private InterpolableLong volumeSize // TODO: If you're creating the volume from a snapshot and don't specify a volume size, the default is the snapshot size.

  @ComputedInput
  InterpolableLong getVolumeSize() {
    if (InterpolableLong.Utils.requiresInitialization(volumeSize)) {
      volumeSize = InterpolableLong.Utils.initWithDefault(volumeSize, (Long)null, { noDevice.get() || virtualName.get() }, { Long interpolableValue -> interpolableValue > 0 ? interpolableValue : null} )
      /*ComputeServiceContext context = ContextBuilder.newBuilder('aws-ec2')
        .credentials(accesskeyid, secretkey)
        .modules(ImmutableSet.<Module> of(new Log4JLoggingModule(),
        new SshjSshClientModule()))
        .buildView(ComputeServiceContext.class)



      ContextBuilder d
      ElasticBlockStoreClient ebsClient = EC2Client.class.cast(context.getProviderSpecificContext().getApi())
        .getElasticBlockStoreServices();*/
    }
    volumeSize
  }

  @Optional
  private InterpolableString kmsKeyId

  // TODO: get, set

  @Override
  BlockDevice interpolate(Context context) {
    BlockDevice result = new BlockDevice(
      deleteOnTermination.interpolateValue(context, result)
      deviceName.interpolateValue(context, result)
      encrypted.interpolateValue(context, result)
      iops.interpolateValue(context, result)
      noDevice.interpolateValue(context, result)
      snapshotId.interpolateValue(context, result)
      virtualName.interpolateValue(context, result)
      volumeType.interpolateValue(context, result)
      volumeSize.interpolateValue(context, result)
      kmsKeyId.interpolateValue(context, result)
    )
    result
  }
}
