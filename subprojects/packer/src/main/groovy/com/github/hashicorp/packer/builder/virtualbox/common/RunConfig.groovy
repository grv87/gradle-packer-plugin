package com.github.hashicorp.packer.builder.virtualbox.common

import com.github.hashicorp.packer.engine.types.InterpolableBoolean
import com.github.hashicorp.packer.engine.types.InterpolableObject
import com.github.hashicorp.packer.engine.types.InterpolableString
import com.github.hashicorp.packer.engine.types.InterpolableUnsignedInteger
import groovy.transform.AutoClone
import groovy.transform.AutoCloneStyle
import groovy.transform.CompileStatic
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.Internal

@AutoClone(style = AutoCloneStyle.SIMPLE)
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
