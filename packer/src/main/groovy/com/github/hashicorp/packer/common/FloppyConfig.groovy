package com.github.hashicorp.packer.common

import org.fidata.packer.engine.types.base.InterpolableObject
import org.fidata.packer.engine.types.InterpolableStringArray
import groovy.transform.CompileStatic

@CompileStatic
class FloppyConfig extends InterpolableObject {
  InterpolableStringArray floppyFiles

  InterpolableStringArray floppyDirectories
}
