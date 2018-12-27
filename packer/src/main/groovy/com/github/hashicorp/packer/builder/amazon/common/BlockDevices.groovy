package com.github.hashicorp.packer.builder.amazon.common

import org.fidata.packer.engine.annotations.Inline
import org.fidata.packer.engine.types.base.InterpolableObject
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
