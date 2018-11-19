package com.github.hashicorp.packer.builder.virtualbox.common

import com.github.hashicorp.packer.engine.types.InterpolableObject
import com.github.hashicorp.packer.engine.types.InterpolableString
import groovy.transform.CompileStatic

@CompileStatic
class ExportConfig extends InterpolableObject {
  // TODO: Internal ?
  InterpolableString format // TODO: Enum ?
}
