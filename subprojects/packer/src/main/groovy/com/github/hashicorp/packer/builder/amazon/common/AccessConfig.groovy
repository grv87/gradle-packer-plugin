package com.github.hashicorp.packer.builder.amazon.common

import com.fasterxml.jackson.annotation.JsonProperty
import com.github.hashicorp.packer.engine.annotations.ComputedInput
import com.github.hashicorp.packer.engine.types.InterpolableBoolean
import com.github.hashicorp.packer.engine.types.InterpolableObject
import com.github.hashicorp.packer.engine.types.InterpolableString
import groovy.transform.AutoClone
import groovy.transform.AutoCloneStyle
import groovy.transform.CompileStatic
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.Internal
import org.gradle.api.tasks.Optional

@AutoClone(style = AutoCloneStyle.SIMPLE)
@CompileStatic
class AccessConfig extends InterpolableObject {
  @Internal
  InterpolableString accessKey

  @Input
  @Optional
  InterpolableString customEndpointEc2

  @Internal
  InterpolableString mfaCode

  @JsonProperty('profile')
  @Internal
  InterpolableString profileName

  @JsonProperty('region')
  @Input
  // required
  InterpolableString rawRegion

  @Internal
  InterpolableString secretKey

  @JsonProperty('skip_region_validation')
  @Internal
  InterpolableBoolean skipValidation = InterpolableBoolean.withDefault(false)

  // ?
  InterpolableBoolean skipMetadataApiCheck

  @Internal
  InterpolableString token // This will also be read from the AWS_SESSION_TOKEN environmental variable

  @Override
  protected void doInterpolate() {
    accessKey?.interpolate context
    customEndpointEc2?.interpolate context
    mfaCode?.interpolate context
    profileName?.interpolate context
    rawRegion?.interpolate context
    secretKey?.interpolate context
    skipValidation?.interpolate context
    skipMetadataApiCheck?.interpolate context
    token?.interpolate context
  }

  @ComputedInput
  String getOwner() {
    // TODO
  }
}
