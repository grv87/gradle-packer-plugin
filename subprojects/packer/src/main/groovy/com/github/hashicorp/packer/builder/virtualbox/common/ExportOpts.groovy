package com.github.hashicorp.packer.builder.virtualbox.common

import com.github.hashicorp.packer.engine.types.InterpolableObject
import com.github.hashicorp.packer.engine.types.InterpolableStringArray
import org.gradle.api.tasks.Input

class ExportOpts extends InterpolableObject {
  @Input
  InterpolableStringArray exportOpts
}
