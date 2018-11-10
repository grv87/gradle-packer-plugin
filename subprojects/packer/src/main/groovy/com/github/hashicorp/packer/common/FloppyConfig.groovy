package com.github.hashicorp.packer.common

import com.github.hashicorp.packer.engine.types.InterpolableObject
import com.github.hashicorp.packer.engine.types.InterpolableStringArray
import groovy.transform.AutoClone
import groovy.transform.AutoCloneStyle
import groovy.transform.CompileStatic

@AutoClone(style = AutoCloneStyle.SIMPLE)
@CompileStatic
class FloppyConfig extends InterpolableObject {
  InterpolableStringArray floppyFiles

  InterpolableStringArray floppyDirectories
}
