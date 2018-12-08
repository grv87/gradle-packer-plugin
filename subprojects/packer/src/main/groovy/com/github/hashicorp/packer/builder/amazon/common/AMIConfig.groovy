package com.github.hashicorp.packer.builder.amazon.common

import com.fasterxml.jackson.annotation.JsonProperty
import com.github.hashicorp.packer.engine.annotations.AutoImplement
import com.github.hashicorp.packer.engine.types.InterpolableBoolean
import com.github.hashicorp.packer.engine.types.InterpolableObject
import com.github.hashicorp.packer.engine.types.InterpolableString
import com.github.hashicorp.packer.engine.types.InterpolableStringArray
import groovy.transform.CompileStatic

@AutoImplement
@CompileStatic
interface AMIConfig extends InterpolableObject {
  InterpolableString amiName

  InterpolableString amiDescription

  @JsonProperty('ami_virtualization_type')
  InterpolableString amiVirtType // TODO: Enum

  InterpolableStringArray amiUsers

  InterpolableStringArray amiGroups

  InterpolableStringArray amiProductCodes

  InterpolableStringArray amiRegions

  @JsonProperty('skip_region_validation')
  InterpolableBoolean amiSkipRegionValidation

  @JsonProperty('tags')
  TagMap amiTags

  @JsonProperty('ena_support')
  InterpolableBoolean AMIENASupport

  @JsonProperty('sriov_support')
  InterpolableBoolean AMISriovNetSupport

  @JsonProperty('force_deregister')
  InterpolableBoolean AMIForceDeregister

  @JsonProperty('force_delete_snapshot')
  InterpolableBoolean AMIForceDeleteSnapshot

  @JsonProperty('encrypt_boot')
  InterpolableBoolean AMIEncryptBootVolume

  @JsonProperty('kms_key_id')
  InterpolableString AMIKmsKeyId

  @JsonProperty('region_kms_key_ids')
  Map<InterpolableString, InterpolableString> AMIRegionKMSKeyIDs

  TagMap snapshotTags

  InterpolableStringArray snapshotUsers

  InterpolableStringArray snapshotGroups
}
