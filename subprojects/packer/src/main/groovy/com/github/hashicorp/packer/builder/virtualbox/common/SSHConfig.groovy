package com.github.hashicorp.packer.builder.virtualbox.common

import com.github.hashicorp.packer.engine.annotations.Inline
import com.github.hashicorp.packer.engine.types.InterpolableObject
import com.github.hashicorp.packer.engine.types.InterpolableUnsignedInteger
import com.github.hashicorp.packer.helper.Communicator

class SSHConfig extends InterpolableObject {
  @Inline
  Communicator comm

  InterpolableUnsignedInteger sshHostPortMin
  InterpolableUnsignedInteger sshHostPortMax
  InterpolableUnsignedInteger sshSkipNatMapping
}
