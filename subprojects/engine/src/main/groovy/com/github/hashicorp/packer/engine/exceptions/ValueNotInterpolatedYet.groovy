package com.github.hashicorp.packer.engine.exceptions

class ValueNotInterpolatedYet extends IllegalStateException {
  ValueNotInterpolatedYet() {
    super('Value is not interpolated yet')
  }
}
