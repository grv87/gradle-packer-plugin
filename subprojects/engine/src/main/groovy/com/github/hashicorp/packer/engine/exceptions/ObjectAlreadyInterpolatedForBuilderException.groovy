package com.github.hashicorp.packer.engine.exceptions

import groovy.transform.CompileStatic

@CompileStatic
class ObjectAlreadyInterpolatedForBuilderException extends IllegalStateException {
  ObjectAlreadyInterpolatedForBuilderException() {
    super('Оbject is already interpolated for builder')
  }
}
