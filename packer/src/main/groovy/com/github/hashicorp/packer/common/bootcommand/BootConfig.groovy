package com.github.hashicorp.packer.common.bootcommand

import com.fasterxml.jackson.annotation.JsonProperty
import org.fidata.packer.engine.types.InterpolableDuration
import org.fidata.packer.engine.types.base.InterpolableObject
import org.fidata.packer.engine.types.InterpolableString
import groovy.transform.CompileStatic
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.Internal

@CompileStatic
class BootConfig extends InterpolableObject {
  @JsonProperty('boot_keygroup_interval')
  @Internal
  InterpolableDuration bootGroupInterval

  @Internal
  InterpolableDuration bootWait

  @Input
  InterpolableString bootCommand
}
