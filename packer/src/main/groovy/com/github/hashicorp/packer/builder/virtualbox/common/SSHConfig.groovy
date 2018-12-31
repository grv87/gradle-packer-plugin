package com.github.hashicorp.packer.builder.virtualbox.common

import org.fidata.packer.engine.annotations.ConnectionSetting
import org.fidata.packer.engine.annotations.Inline
import org.fidata.packer.engine.types.base.InterpolableObject
import org.fidata.packer.engine.types.InterpolableUnsignedInteger
import com.github.hashicorp.packer.helper.Communicator
import groovy.transform.CompileStatic

@CompileStatic
abstract class SSHConfig implements InterpolableObject<SSHConfig> {
  @Inline
  abstract Communicator getComm()

  @ConnectionSetting
  abstract InterpolableUnsignedInteger getSshHostPortMin()

  @ConnectionSetting
  abstract InterpolableUnsignedInteger getSshHostPortMax()

  @ConnectionSetting
  abstract InterpolableUnsignedInteger getSshSkipNatMapping()
}
