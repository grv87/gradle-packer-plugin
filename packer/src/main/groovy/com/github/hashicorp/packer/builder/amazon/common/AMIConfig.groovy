/*
 * AccessConfig class
 * Copyright © 2018-2019  Basil Peace
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
import org.fidata.packer.engine.types.InterpolableBoolean
import org.fidata.packer.engine.types.base.InterpolableObject
import org.fidata.packer.engine.types.InterpolableString
import org.fidata.packer.engine.types.InterpolableStringArray
import groovy.transform.CompileStatic

@AutoImplement
@CompileStatic
abstract class AMIConfig implements InterpolableObject<AMIConfig> {
  abstract InterpolableString getAmiName()

  abstract InterpolableString getAmiDescription()

  @JsonProperty('ami_virtualization_type')
  abstract InterpolableString getAmiVirtType() // TODO: Enum

  abstract InterpolableStringArray getAmiUsers()

  abstract InterpolableStringArray getAmiGroups()

  abstract InterpolableStringArray getAmiProductCodes()

  abstract InterpolableStringArray getAmiRegions()

  @JsonProperty('skip_region_validation')
  abstract InterpolableBoolean getAmiSkipRegionValidation()

  @JsonProperty('tags')
  abstract TagMap getAmiTags()

  @JsonProperty('ena_support')
  abstract InterpolableBoolean getAMIENASupport()

  @JsonProperty('sriov_support')
  abstract InterpolableBoolean getAMISriovNetSupport()

  @JsonProperty('force_deregister')
  abstract InterpolableBoolean getAMIForceDeregister()

  @JsonProperty('force_delete_snapshot')
  abstract InterpolableBoolean getAMIForceDeleteSnapshot()

  @JsonProperty('encrypt_boot')
  abstract InterpolableBoolean getAMIEncryptBootVolume()

  @JsonProperty('kms_key_id')
  abstract InterpolableString getAMIKmsKeyId()

  @JsonProperty('region_kms_key_ids')
  abstract Map<InterpolableString, InterpolableString> getAMIRegionKMSKeyIDs()

  abstract TagMap getSnapshotTags()

  abstract InterpolableStringArray getSnapshotUsers()

  abstract InterpolableStringArray getSnapshotGroups()
}
