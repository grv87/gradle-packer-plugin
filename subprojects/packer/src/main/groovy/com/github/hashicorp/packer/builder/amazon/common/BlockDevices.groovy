package com.github.hashicorp.packer.builder.amazon.common

import com.github.hashicorp.packer.engine.annotations.Inline
import com.github.hashicorp.packer.engine.types.InterpolableObject
import groovy.transform.CompileStatic

@CompileStatic
// DONE
class BlockDevices extends InterpolableObject {
  @Inline
  AMIBlockDevices amiBlockDevices

  @Inline
  LaunchBlockDevices launchBlockDevices

  @Override
  protected void doInterpolate() {
    amiBlockDevices.interpolate context
    launchBlockDevices.interpolate context
  }
}
