package com.github.hashicorp.packer.builder.amazon.common

import com.fasterxml.jackson.annotation.JsonProperty
import com.github.hashicorp.packer.engine.annotations.AutoImplement
import com.github.hashicorp.packer.engine.types.InterpolableObject
import groovy.transform.CompileStatic
import org.gradle.api.tasks.Nested

@AutoImplement
@CompileStatic
// DONE
interface AMIBlockDevices extends InterpolableObject {
  @JsonProperty('ami_block_device_mappings')
  @Nested
  List<BlockDevice> amiMappings
}
