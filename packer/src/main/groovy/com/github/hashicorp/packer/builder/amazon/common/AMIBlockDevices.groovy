package com.github.hashicorp.packer.builder.amazon.common

import com.fasterxml.jackson.annotation.JsonProperty
import org.fidata.packer.engine.annotations.AutoImplement
import org.fidata.packer.engine.types.base.InterpolableObject
import groovy.transform.CompileStatic
import org.gradle.api.tasks.Nested

@AutoImplement
@CompileStatic
// DONE
abstract class AMIBlockDevices implements InterpolableObject<AMIBlockDevices> {
  @JsonProperty('ami_block_device_mappings')
  @Nested
  abstract List<BlockDevice> getAmiMappings()
}
