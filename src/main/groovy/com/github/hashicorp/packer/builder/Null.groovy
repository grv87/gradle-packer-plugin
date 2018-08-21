package com.github.hashicorp.packer.builder

import groovy.transform.AutoClone
import groovy.transform.AutoCloneStyle
import groovy.transform.CompileStatic
import com.github.hashicorp.packer.template.Builder
import com.github.hashicorp.packer.template.annotations.Inline
import com.github.hashicorp.packer.helper.Communicator

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
