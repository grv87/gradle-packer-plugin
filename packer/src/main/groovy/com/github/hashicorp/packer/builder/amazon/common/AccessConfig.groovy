package com.github.hashicorp.packer.builder.amazon.common

import com.fasterxml.jackson.annotation.JsonProperty
import org.fidata.packer.engine.annotations.AutoImplement
import org.fidata.packer.engine.annotations.ComputedInput
import org.fidata.packer.engine.annotations.Default
import org.fidata.packer.engine.types.InterpolableBoolean
import org.fidata.packer.engine.types.base.InterpolableObject
import org.fidata.packer.engine.types.InterpolableString
import groovy.transform.CompileStatic
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.Internal
import org.gradle.api.tasks.Optional

@AutoImplement
@CompileStatic
abstract class AccessConfig implements InterpolableObject<AccessConfig> {
  @Internal
  abstract InterpolableString getAccessKey()

  @Input
  @Optional
  abstract InterpolableString getCustomEndpointEc2()

  @Internal
  abstract InterpolableString getMfaCode()

  @JsonProperty('profile')
  @Internal
  abstract InterpolableString getProfileName()

  @JsonProperty('region')
  @Input
  // required
  abstract InterpolableString getRawRegion()

  @Internal
  abstract InterpolableString getSecretKey()

  @JsonProperty('skip_region_validation')
  @Default({Boolean.FALSE})
  @Internal
  abstract InterpolableBoolean getSkipValidation()

  // ?
  abstract InterpolableBoolean getSkipMetadataApiCheck()

  @Internal
  abstract InterpolableString token // TODO: This will also be read from the AWS_SESSION_TOKEN environmental variable

  @ComputedInput
  String getOwner() {
    // TODO
  }
}
