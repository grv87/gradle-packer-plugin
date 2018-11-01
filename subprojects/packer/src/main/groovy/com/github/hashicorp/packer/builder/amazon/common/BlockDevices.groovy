package com.github.hashicorp.packer.builder.amazon.common

import com.github.hashicorp.packer.engine.annotations.Inline
import com.github.hashicorp.packer.engine.types.InterpolableObject
import groovy.transform.AutoClone
import groovy.transform.AutoCloneStyle
import groovy.transform.CompileStatic

@AutoClone(style = AutoCloneStyle.SIMPLE)
@CompileStatic
class BlockDevices extends InterpolableObject {
  @Inline
  AMIBlockDevices amiBlockDevices

  @Inline
  LaunchBlockDevices launchBlockDevices
}
