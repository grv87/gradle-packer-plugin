package com.github.hashicorp.packer.common.bootcommand

import com.fasterxml.jackson.annotation.JsonProperty
import org.fidata.packer.engine.annotations.AutoImplement
import org.fidata.packer.engine.annotations.Timing
import org.fidata.packer.engine.types.InterpolableDuration
import org.fidata.packer.engine.types.base.InterpolableObject
import org.fidata.packer.engine.types.InterpolableString
import groovy.transform.CompileStatic
import org.gradle.api.tasks.Input

@AutoImplement
@CompileStatic
abstract class BootConfig implements InterpolableObject<BootConfig> {
  @JsonProperty('boot_keygroup_interval')
  @Timing
  abstract InterpolableDuration getBootGroupInterval()

  @Timing
  abstract InterpolableDuration getBootWait()

  @Input
  abstract InterpolableString getBootCommand()
}
