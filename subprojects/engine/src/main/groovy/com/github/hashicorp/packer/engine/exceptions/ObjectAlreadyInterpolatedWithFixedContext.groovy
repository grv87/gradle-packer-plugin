package com.github.hashicorp.packer.engine.exceptions

import groovy.transform.CompileStatic

@CompileStatic
class ObjectAlreadyInterpolatedWithFixedContext extends IllegalStateException {
  ObjectAlreadyInterpolatedWithFixedContext() {
    super('Object is already interpolated with fixed context')
  }
}
