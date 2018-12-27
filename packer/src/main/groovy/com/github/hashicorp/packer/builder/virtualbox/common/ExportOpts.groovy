package com.github.hashicorp.packer.builder.virtualbox.common

import org.fidata.packer.engine.types.base.InterpolableObject
import org.fidata.packer.engine.types.InterpolableStringArray
import groovy.transform.CompileStatic
import org.gradle.api.tasks.Input

@CompileStatic
class ExportOpts extends InterpolableObject {
  @Input
  InterpolableStringArray exportOpts
}
