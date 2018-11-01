package com.github.hashicorp.packer.common.bootcommand

import com.fasterxml.jackson.annotation.JsonProperty
import com.github.hashicorp.packer.engine.types.InterpolableDuration
import com.github.hashicorp.packer.engine.types.InterpolableObject
import com.github.hashicorp.packer.engine.types.InterpolableString
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.Internal

class BootConfig extends InterpolableObject {
  @JsonProperty('boot_keygroup_interval')
  @Internal
  InterpolableDuration bootGroupInterval

  @Internal
  InterpolableDuration bootWait

  @Input
  InterpolableString bootCommand
}
