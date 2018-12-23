package com.github.hashicorp.packer.common

import com.github.hashicorp.packer.engine.types.InterpolableInputDirectory
import com.github.hashicorp.packer.engine.types.base.InterpolableObject
import com.github.hashicorp.packer.engine.types.InterpolableUnsignedInteger
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
