package com.github.hashicorp.packer.builder.amazon.common

import com.fasterxml.jackson.annotation.JsonProperty
import org.fidata.packer.engine.types.base.InterpolableObject
import groovy.transform.CompileStatic
import org.gradle.api.tasks.Nested

@CompileStatic
// DONE
class LaunchBlockDevices extends InterpolableObject {
  @JsonProperty('launch_block_device_mappings')
  @Nested
  List<BlockDevice> launchMappings

  @Override
  protected void doInterpolate() {
    launchMappings*.interpolate context
  }
}
