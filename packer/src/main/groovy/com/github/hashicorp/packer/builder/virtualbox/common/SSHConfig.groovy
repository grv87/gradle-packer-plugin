package com.github.hashicorp.packer.builder.virtualbox.common

import org.fidata.packer.engine.annotations.Inline
import org.fidata.packer.engine.types.base.InterpolableObject
import org.fidata.packer.engine.types.InterpolableUnsignedInteger
import com.github.hashicorp.packer.helper.Communicator
import groovy.transform.CompileStatic
import org.gradle.api.tasks.Internal

@CompileStatic
class SSHConfig extends InterpolableObject {
  @Inline
  Communicator comm

  @Internal
  InterpolableUnsignedInteger sshHostPortMin

  @Internal
  InterpolableUnsignedInteger sshHostPortMax

  @Internal
  InterpolableUnsignedInteger sshSkipNatMapping
}
