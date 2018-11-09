package com.github.hashicorp.packer.builder.amazon.common

import com.fasterxml.jackson.annotation.JsonProperty
import com.github.hashicorp.packer.engine.annotations.Inline
import com.github.hashicorp.packer.engine.types.InterpolableBoolean
import com.github.hashicorp.packer.engine.types.InterpolableDuration
import com.github.hashicorp.packer.engine.types.InterpolableLong
import com.github.hashicorp.packer.engine.types.InterpolableObject
import com.github.hashicorp.packer.engine.types.InterpolableString
import com.github.hashicorp.packer.engine.types.InterpolableStringArray
import com.github.hashicorp.packer.helper.Communicator
import groovy.transform.AutoClone
import groovy.transform.AutoCloneStyle
import groovy.transform.CompileStatic
import org.gradle.api.tasks.Internal

@AutoClone(style = AutoCloneStyle.SIMPLE)
@CompileStatic
class RunConfig extends InterpolableObject {
  @Internal
  InterpolableBoolean associatePublicIpAddress

  @Internal
  InterpolableString availabilityZone

  @Internal
  InterpolableLong blockDurationMinutes

  @Internal
  InterpolableBoolean disableStopInstance

  @Internal
  InterpolableBoolean enableT2Unlimited

  @Internal
  InterpolableString iamInstanceProfile

  @JsonProperty('shutdown_behavior')
  @Internal
  InterpolableString instanceInitiatedShutdownBehavior

  @Internal
  InterpolableString instanceType

  @Internal
  TagMap /* Packer doesn't use TagMap here */ runTags

  @Internal
  InterpolableString securityGroupId

  @Internal
  InterpolableStringArray securityGroupIds

  @Internal
  InterpolableString sourceAmi

  @Internal
  AmiFilterOptions sourceAmiFilter

  @Internal
  InterpolableString spotPrice

  @Internal
  InterpolableString spotPriceAutoProduct

  @Internal
  Map<InterpolableString, InterpolableString> spotTags

  @Internal
  InterpolableString subnetId

  @Internal
  InterpolableString temporaryKeyPairName

  @JsonProperty('temporary_security_group_source_cidr')
  @Internal
  InterpolableString temporarySGSourceCidr

  @Internal
  InterpolableString userData

  @Internal
  InterpolableString userDataFile

  @Internal
  InterpolableString vpcId

  @Internal
  InterpolableDuration windowsPasswordTimeout

  @Inline
  Communicator comm
}