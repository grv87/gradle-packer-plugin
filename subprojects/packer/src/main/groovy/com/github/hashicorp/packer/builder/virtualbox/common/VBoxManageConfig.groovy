package com.github.hashicorp.packer.builder.virtualbox.common

import com.fasterxml.jackson.annotation.JsonProperty
import com.github.hashicorp.packer.engine.types.InterpolableObject
import com.github.hashicorp.packer.engine.types.InterpolableString
import groovy.transform.AutoClone
import groovy.transform.AutoCloneStyle
import groovy.transform.CompileStatic
import org.gradle.api.tasks.Input

@AutoClone(style = AutoCloneStyle.SIMPLE)
@CompileStatic
class VBoxManageConfig extends InterpolableObject {
  @JsonProperty('vboxmanage')
  @Input
  List<List<InterpolableString>> vboxManage
}
