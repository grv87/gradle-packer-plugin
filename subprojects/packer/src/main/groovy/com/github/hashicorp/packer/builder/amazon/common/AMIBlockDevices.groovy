package com.github.hashicorp.packer.builder.amazon.common

import com.fasterxml.jackson.annotation.JsonProperty
import com.github.hashicorp.packer.engine.types.InterpolableObject
import groovy.transform.AutoClone
import groovy.transform.AutoCloneStyle
import groovy.transform.CompileStatic
import org.gradle.api.tasks.Nested

@AutoClone(style = AutoCloneStyle.SIMPLE)
@CompileStatic
// DONE
class AMIBlockDevices extends InterpolableObject {
  @JsonProperty('ami_block_device_mappings')
  @Nested
  List<BlockDevice> amiMappings

  @Override
  protected void doInterpolate() {
    amiMappings*.interpolate context
  }
}
