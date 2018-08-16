package org.fidata.gradle.packer.template.internal

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonValue
import groovy.transform.CompileStatic
import org.fidata.gradle.packer.template.Context
import org.gradle.api.tasks.Internal

@CompileStatic
abstract class InterpolableSinglePrimitive<Source, Target> extends InterpolablePrimitive<Target> {
  @JsonValue
  @Internal
  Source rawValue

  @JsonCreator
  InterpolableSinglePrimitive(Source rawValue) {
    this.rawValue = rawValue
  }
}
