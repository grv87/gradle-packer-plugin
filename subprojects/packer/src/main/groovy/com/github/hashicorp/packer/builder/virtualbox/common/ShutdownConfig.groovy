package com.github.hashicorp.packer.builder.virtualbox.common

import com.github.hashicorp.packer.engine.types.InterpolableDuration
import com.github.hashicorp.packer.engine.types.InterpolableObject
import com.github.hashicorp.packer.engine.types.InterpolableString
import groovy.transform.CompileStatic
import org.gradle.api.tasks.Internal

@CompileStatic
class ShutdownConfig extends InterpolableObject {
  @Internal
  // CAVEAT: It is expected that shutdown command doesn't have any side effects
  // and so doesn't influence the result of the build
  InterpolableString shutdownCommand

  @Internal
  InterpolableDuration shutdownTimeout

  @Internal
  InterpolableDuration postShutdownDelay
}
