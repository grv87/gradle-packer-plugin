package com.github.hashicorp.packer.builder.amazon.common

import com.fasterxml.jackson.annotation.JsonProperty
import com.github.hashicorp.packer.engine.annotations.AutoImplement
import com.github.hashicorp.packer.engine.annotations.ComputedInput
import com.github.hashicorp.packer.engine.types.InterpolableBoolean
import com.github.hashicorp.packer.engine.types.base.InterpolableObject
import com.github.hashicorp.packer.engine.types.InterpolableString
import groovy.transform.CompileStatic
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.Internal
import org.gradle.api.tasks.Optional

@AutoImplement
@CompileStatic
interface AccessConfig extends InterpolableObject {
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
  InterpolableString token // TODO: This will also be read from the AWS_SESSION_TOKEN environmental variable

  abstract class AccessConfigImpl implements AccessConfig {
    @ComputedInput
    String getOwner() {
      // TODO
    }
  }
}
