package com.github.hashicorp.packer.engine.exceptions

import groovy.transform.CompileStatic

@CompileStatic
class ObjectAlreadyInterpolatedForBuilder extends IllegalStateException {
  ObjectAlreadyInterpolatedForBuilder() {
    super('Оbject is already interpolated for builder')
  }
}
