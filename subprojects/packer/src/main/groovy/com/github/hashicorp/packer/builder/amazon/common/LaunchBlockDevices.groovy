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
class LaunchBlockDevices extends InterpolableObject {
  @JsonProperty('launch_block_device_mappings')
  @Nested
  List<BlockDevice> launchMappings

  @Override
  protected void doInterpolate() {
    launchMappings*.interpolate context
  }
}
