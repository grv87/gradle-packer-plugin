package com.github.hashicorp.packer.builder.virtualbox.common

import com.github.hashicorp.packer.engine.types.InterpolableObject
import com.github.hashicorp.packer.engine.types.InterpolableStringArray
import groovy.transform.AutoClone
import groovy.transform.AutoCloneStyle
import groovy.transform.CompileStatic
import org.gradle.api.tasks.Input

@AutoClone(style = AutoCloneStyle.SIMPLE)
@CompileStatic
class ExportOpts extends InterpolableObject {
  @Input
  InterpolableStringArray exportOpts
}
