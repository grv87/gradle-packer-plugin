package org.fidata.packer.engine.exceptions

import groovy.transform.CompileStatic

@CompileStatic
class ObjectAlreadyInterpolatedWithFixedContextException extends UnsupportedOperationException {
  ObjectAlreadyInterpolatedWithFixedContextException() {
    super('Object is already interpolated with fixed context')
  }
}
