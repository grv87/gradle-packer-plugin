package com.github.hashicorp.packer.common

import org.fidata.packer.engine.types.InterpolableInputDirectory
import org.fidata.packer.engine.types.base.InterpolableObject
import org.fidata.packer.engine.types.InterpolableUnsignedInteger
import groovy.transform.CompileStatic
import org.gradle.api.tasks.Internal
import org.gradle.api.tasks.Nested

@CompileStatic
class HTTPConfig extends InterpolableObject {
  @Nested
  InterpolableInputDirectory httpDir

  @Internal
  InterpolableUnsignedInteger httpPortMin

  @Internal
  InterpolableUnsignedInteger httpPortMax
}
