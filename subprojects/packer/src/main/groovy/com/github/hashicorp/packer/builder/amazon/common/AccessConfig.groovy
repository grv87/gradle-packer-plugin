package com.github.hashicorp.packer.builder.amazon.common

import com.fasterxml.jackson.annotation.JsonProperty
import com.github.hashicorp.packer.engine.types.InterpolableBoolean
import com.github.hashicorp.packer.engine.types.InterpolableObject
import com.github.hashicorp.packer.engine.types.InterpolableString
import groovy.transform.AutoClone
import groovy.transform.AutoCloneStyle
import groovy.transform.CompileStatic

@AutoClone(style = AutoCloneStyle.SIMPLE)
@CompileStatic
class AccessConfig extends InterpolableObject {
  InterpolableString accessKey
  InterpolableString customEndpointEc2
  InterpolableString mfaCode
  @JsonProperty('profile')
  InterpolableString profileName
  @JsonProperty('region')
  InterpolableString rawRegion
  InterpolableString secretKey
  @JsonProperty('skip_region_validation')
  InterpolableBoolean skipValidation
  InterpolableBoolean skipMetadataApiCheck
  InterpolableString token
}
