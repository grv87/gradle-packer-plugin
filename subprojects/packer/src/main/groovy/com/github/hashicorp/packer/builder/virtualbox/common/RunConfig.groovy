package com.github.hashicorp.packer.builder.virtualbox.common

import com.github.hashicorp.packer.engine.types.InterpolableBoolean
import com.github.hashicorp.packer.engine.types.base.InterpolableObject
import com.github.hashicorp.packer.engine.types.InterpolableString
import com.github.hashicorp.packer.engine.types.InterpolableUnsignedInteger
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
