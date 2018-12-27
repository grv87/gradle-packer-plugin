package com.github.hashicorp.packer.builder.virtualbox.common

import org.fidata.packer.engine.types.base.InterpolableObject
import org.fidata.packer.engine.types.InterpolableString
import groovy.transform.CompileStatic

@CompileStatic
class ExportConfig extends InterpolableObject {
  // TODO: Internal ?
  InterpolableString format // TODO: Enum ?
}
