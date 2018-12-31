package com.github.hashicorp.packer.builder.virtualbox.common

import org.fidata.packer.engine.annotations.Timing
import org.fidata.packer.engine.types.InterpolableDuration
import org.fidata.packer.engine.types.base.InterpolableObject
import org.fidata.packer.engine.types.InterpolableString
import groovy.transform.CompileStatic
import org.gradle.api.tasks.Internal

@CompileStatic
abstract class ShutdownConfig implements InterpolableObject<ShutdownConfig> {
  @Internal
  // CAVEAT: It is expected that shutdown command doesn't have any side effects
  // and so doesn't influence the result of the build
  abstract InterpolableString getShutdownCommand()

  @Timing
  abstract InterpolableDuration getShutdownTimeout()

  @Timing
  abstract InterpolableDuration getPostShutdownDelay()
}
