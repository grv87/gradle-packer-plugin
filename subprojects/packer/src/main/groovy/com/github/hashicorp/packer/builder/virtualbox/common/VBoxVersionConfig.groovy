package com.github.hashicorp.packer.builder.virtualbox.common

import com.fasterxml.jackson.annotation.JsonProperty
import com.github.hashicorp.packer.engine.annotations.Default
import com.github.hashicorp.packer.engine.types.InterpolableObject
import com.github.hashicorp.packer.engine.types.InterpolableString
import org.gradle.api.tasks.Input

class VBoxVersionConfig extends InterpolableObject {
  @JsonProperty('virtualbox_version_file')
  @Input
  @Default(value = '.vbox_version') // in home dir !
  InterpolableString vboxVersionFile
}
