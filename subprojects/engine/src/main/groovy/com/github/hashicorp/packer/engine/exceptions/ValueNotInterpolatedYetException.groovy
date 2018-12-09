package com.github.hashicorp.packer.engine.exceptions

import groovy.transform.CompileStatic

@CompileStatic
class ValueNotInterpolatedYetException extends UnsupportedOperationException {
  ValueNotInterpolatedYetException() {
    super('Value is not interpolated yet')
  }
}
