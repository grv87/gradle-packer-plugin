/*
 * AccessConfig class
 * Copyright ©  Basil Peace
 *
 * This file is part of gradle-packer-plugin.
 *
 * This plugin is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License
 * as published by the Free Software Foundation, either version 3
 * of the License, or (at your option) any later version.
 *
 * This plugin is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this plugin.  If not, see <https://www.gnu.org/licenses/>.
 *
 * Ported from original Packer code,
 * file builder/amazon/common/access_config.go
 * under the terms of the Mozilla Public License, v. 2.0.
 */
package com.github.hashicorp.packer.builder.amazon.common

import com.fasterxml.jackson.annotation.JsonProperty
import org.fidata.packer.engine.annotations.AutoImplement
import org.fidata.packer.engine.annotations.ComputedInput
import org.fidata.packer.engine.annotations.Credential
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
  @Credential
  abstract InterpolableString getAccessKey()

  @Input
  @Optional
  abstract InterpolableString getCustomEndpointEc2()

  @Credential
  abstract InterpolableString getMfaCode()

  @JsonProperty('profile')
  @Internal // TODO
  abstract InterpolableString getProfileName()

  @JsonProperty('region')
  @Input
  // required
  abstract InterpolableString getRawRegion()

  @Credential
  abstract InterpolableString getSecretKey()

  @JsonProperty('skip_region_validation')
  @Default({Boolean.FALSE})
  @Internal
  abstract InterpolableBoolean getSkipValidation()

  // ?
  abstract InterpolableBoolean getSkipMetadataApiCheck()

  @Credential
  abstract InterpolableString getToken() // TODO: This will also be read from the AWS_SESSION_TOKEN environmental variable

  @ComputedInput
  String getOwner() {
    // TODO
  }
}
