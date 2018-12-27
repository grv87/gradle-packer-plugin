package com.github.hashicorp.packer.builder.virtualbox.common

import com.fasterxml.jackson.annotation.JsonProperty
import org.fidata.packer.engine.types.base.InterpolableObject
import org.fidata.packer.engine.types.InterpolableString
import groovy.transform.CompileStatic
import org.gradle.api.tasks.Input

@CompileStatic
class VBoxVersionConfig extends InterpolableObject {
  @JsonProperty('virtualbox_version_file')
  @Input
  InterpolableString vboxVersionFile = InterpolableString.withDefault('.vbox_version') // TODO: in home dir !
}
