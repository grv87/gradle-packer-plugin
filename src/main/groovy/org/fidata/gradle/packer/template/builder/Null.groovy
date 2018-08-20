package org.fidata.gradle.packer.template.builder

import groovy.transform.AutoClone
import groovy.transform.AutoCloneStyle
import groovy.transform.CompileStatic
import org.fidata.gradle.packer.template.Builder
import org.fidata.gradle.packer.template.annotations.Inline
import org.fidata.gradle.packer.template.helper.Communicator

@AutoClone(style = AutoCloneStyle.SIMPLE)
@CompileStatic
class Null extends Builder {
  @Inline
  Communicator commConfig

  @Override
  protected void doInterpolate() {
    super.doInterpolate()
    commConfig.interpolate context
  }
}
