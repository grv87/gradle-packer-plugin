package com.github.hashicorp.packer.common

import com.github.hashicorp.packer.engine.types.InterpolablePath
import com.github.hashicorp.packer.engine.types.InterpolableObject
import com.github.hashicorp.packer.engine.types.InterpolableUnsignedInteger
import org.gradle.api.tasks.InputDirectory
import org.gradle.api.tasks.Internal

class HTTPConfig extends InterpolableObject {
  @InputDirectory
  InterpolablePath httpDir

  @Internal
  InterpolableUnsignedInteger httpPortMin

  @Internal
  InterpolableUnsignedInteger httpPortMax
}
