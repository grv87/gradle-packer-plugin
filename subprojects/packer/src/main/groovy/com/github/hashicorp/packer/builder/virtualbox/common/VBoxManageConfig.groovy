package com.github.hashicorp.packer.builder.virtualbox.common

import com.fasterxml.jackson.annotation.JsonProperty
import com.github.hashicorp.packer.engine.types.InterpolableObject
import com.github.hashicorp.packer.engine.types.InterpolableString
import org.gradle.api.tasks.Input

class VBoxManageConfig extends InterpolableObject {
  @JsonProperty('vboxmanage')
  @Input
  List<List<InterpolableString>> vboxManage
}