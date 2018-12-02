package com.github.hashicorp.packer.engine.exceptions

class ValueNotInterpolatedYetException extends UnsupportedOperationException {
  ValueNotInterpolatedYetException() {
    super('Value is not interpolated yet')
  }
}
