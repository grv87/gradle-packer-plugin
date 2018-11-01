package com.github.hashicorp.packer.common

import com.github.hashicorp.packer.engine.types.InterpolableObject
import com.github.hashicorp.packer.engine.types.InterpolableStringArray

class FloppyConfig extends InterpolableObject {
  InterpolableStringArray floppyFiles

  InterpolableStringArray floppyDirectories
}
