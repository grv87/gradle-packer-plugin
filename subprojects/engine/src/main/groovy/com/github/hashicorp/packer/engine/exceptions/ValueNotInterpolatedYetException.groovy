package com.github.hashicorp.packer.engine.exceptions

class ValueNotInterpolatedYetException extends IllegalStateException {
  ValueNotInterpolatedYetException() {
    super('Value is not interpolated yet')
  }
}
