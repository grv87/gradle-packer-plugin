package com.github.hashicorp.packer.builder.amazon.common

import com.fasterxml.jackson.annotation.JsonProperty
import org.fidata.packer.engine.annotations.AutoImplement
import org.fidata.packer.engine.annotations.ExtraProcessed
import org.fidata.packer.engine.annotations.LaunchedVMConfiguration
import org.fidata.packer.engine.annotations.Timing
import org.fidata.packer.engine.annotations.Inline
import org.fidata.packer.engine.types.InterpolableAWSEC2InstanceType
import org.fidata.packer.engine.types.InterpolableBoolean
import org.fidata.packer.engine.types.InterpolableDuration
import org.fidata.packer.engine.types.InterpolableLong
import org.fidata.packer.engine.types.base.InterpolableObject
import org.fidata.packer.engine.types.InterpolableString
import org.fidata.packer.engine.types.InterpolableStringArray
import com.github.hashicorp.packer.helper.Communicator
import groovy.transform.CompileStatic
import org.gradle.api.tasks.Internal

@AutoImplement
@CompileStatic
abstract class RunConfig implements InterpolableObject<RunConfig> {
  @LaunchedVMConfiguration
  abstract InterpolableBoolean getAssociatePublicIpAddress()

  @LaunchedVMConfiguration
  abstract InterpolableString getAvailabilityZone()

  @Internal
  abstract InterpolableLong getBlockDurationMinutes()

  @Internal
  abstract InterpolableBoolean getDisableStopInstance()

  @Internal
  abstract InterpolableBoolean getEnableT2Unlimited()

  @LaunchedVMConfiguration // TODO
  abstract InterpolableString getIamInstanceProfile()

  @JsonProperty('shutdown_behavior')
  @Internal
  abstract InterpolableString getInstanceInitiatedShutdownBehavior()

  @LaunchedVMConfiguration
  @ExtraProcessed
  abstract InterpolableAWSEC2InstanceType getInstanceType()

  @LaunchedVMConfiguration
  abstract TagMap /* Packer doesn't use TagMap here */ getRunTags()

  @LaunchedVMConfiguration
  abstract InterpolableString getSecurityGroupId()

  @LaunchedVMConfiguration
  abstract InterpolableStringArray getSecurityGroupIds()

  @ExtraProcessed
  abstract InterpolableString getSourceAmi()

  @ExtraProcessed
  abstract AmiFilterOptions getSourceAmiFilter()

  @Internal
  abstract InterpolableString getSpotPrice()

  @Internal
  abstract InterpolableString getSpotPriceAutoProduct()

  @Internal
  abstract Map<InterpolableString, InterpolableString> getSpotTags()

  @LaunchedVMConfiguration
  abstract InterpolableString getSubnetId()

  @Internal
  abstract InterpolableString getTemporaryKeyPairName()

  @JsonProperty('temporary_security_group_source_cidr')
  @Internal
  abstract InterpolableString getTemporarySGSourceCidr()

  @Internal
  abstract InterpolableString getUserData()

  @Internal
  abstract InterpolableString getUserDataFile()

  @LaunchedVMConfiguration
  abstract InterpolableString getVpcId()

  @Timing
  abstract InterpolableDuration getWindowsPasswordTimeout()

  @Inline
  abstract Communicator getComm()
}
