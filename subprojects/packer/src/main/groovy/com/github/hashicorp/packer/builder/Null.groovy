package com.github.hashicorp.packer.builder

import groovy.transform.CompileStatic
import com.github.hashicorp.packer.template.Builder
import com.github.hashicorp.packer.engine.annotations.Inline
import com.github.hashicorp.packer.helper.Communicator

@CompileStatic
class Null extends Builder {
  @Inline
  Communicator commConfig

  @Override
  protected void doInterpolate() {
    super.doInterpolate()
    commConfig.interpolate context
  }

  static {
    SUBTYPE_REGISTRY.registerSubtype 'null', Null
  }
}
