package com.github.hashicorp.packer.engine.exceptions

import groovy.transform.CompileStatic

@CompileStatic
class InvalidRawValueClass extends IllegalStateException {
  InvalidRawValueClass(Object rawValue) {
    super(sprintf('Invalid rawValue: %s of class: %s', [rawValue, rawValue.class]))
  }
}
