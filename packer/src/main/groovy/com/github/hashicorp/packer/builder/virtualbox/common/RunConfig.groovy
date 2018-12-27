package com.github.hashicorp.packer.builder.virtualbox.common

import org.fidata.packer.engine.types.InterpolableBoolean
import org.fidata.packer.engine.types.base.InterpolableObject
import org.fidata.packer.engine.types.InterpolableString
import org.fidata.packer.engine.types.InterpolableUnsignedInteger
import groovy.transform.CompileStatic
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.Internal

@CompileStatic
class RunConfig extends InterpolableObject {
  @Internal // TOTEST
  InterpolableBoolean headless

  @Input // TOTEST
  InterpolableString vrdpBindAddress

  @Input // TOTEST
  InterpolableUnsignedInteger vrdpPortMin

  @Input // TOTEST
  InterpolableUnsignedInteger vrdpPortMax
}
