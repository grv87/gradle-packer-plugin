package com.github.hashicorp.packer.engine.exceptions

import groovy.transform.CompileStatic

@CompileStatic
class InvalidRawValueClassException extends IllegalStateException {
  InvalidRawValueClassException(Object rawValue) {
    super(String.format('Invalid rawValue: %s of class: %s', [rawValue, rawValue.class]))
  }
}
