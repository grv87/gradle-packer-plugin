package org.fidata.gradle.packer.template.builder

import com.fasterxml.jackson.annotation.JsonUnwrapped
import groovy.transform.CompileStatic
import org.fidata.gradle.packer.template.Builder
import org.fidata.gradle.packer.template.Context
import org.fidata.gradle.packer.template.helper.Communicator
import org.fidata.gradle.packer.template.internal.TemplateObject
import org.gradle.api.tasks.Nested

@CompileStatic
class Null extends TemplateObject implements Builder {
  @Nested
  @JsonUnwrapped
  Communicator commConfig

  @Override
  protected void doInterpolate(Context ctx) {
    commConfig.interpolate ctx
  }
}
