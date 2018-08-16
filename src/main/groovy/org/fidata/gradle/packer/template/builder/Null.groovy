package org.fidata.gradle.packer.template.builder

import groovy.transform.CompileStatic
import org.fidata.gradle.packer.template.Builder
import org.fidata.gradle.packer.template.Context
import org.fidata.gradle.packer.template.helper.Communicator
import org.fidata.gradle.packer.template.internal.InterpolableObject
import org.fidata.gradle.packer.template.annotations.Inline

@CompileStatic
class Null extends Builder {
  @Inline
  Communicator commConfig

  @Override
  protected void doInterpolate(Context ctx) {
    commConfig.interpolate ctx
  }
}
