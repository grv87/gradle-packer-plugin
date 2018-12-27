package org.fidata.packer.engine.exceptions

import groovy.transform.CompileStatic

/**
 * Most probable causes:
 * 1. Cyclic dependencies via ignoreIf closures
 * 2. Cyclic dependencies via defaultValue closures
 * 3. Cyclic dependencies via Context#templateVariables
 */
@CompileStatic
class RecursiveInterpolationException extends RuntimeException {
  RecursiveInterpolationException() {
    super('Interpolation is made recursively')
  }
}
