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
