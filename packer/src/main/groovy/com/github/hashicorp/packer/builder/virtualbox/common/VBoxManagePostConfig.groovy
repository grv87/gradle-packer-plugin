package com.github.hashicorp.packer.builder.virtualbox.common

import com.fasterxml.jackson.annotation.JsonProperty
import org.fidata.packer.engine.types.base.InterpolableObject
import org.fidata.packer.engine.types.InterpolableString
import groovy.transform.CompileStatic
import org.gradle.api.tasks.Input

@CompileStatic
class VBoxManagePostConfig extends InterpolableObject {
  @JsonProperty('vboxmanage_post')
  @Input
  List<List<InterpolableString>> vboxManagePost
}
