package com.github.hashicorp.packer.engine.exceptions

/**
 * Most probably you have cyclic dependencies in ignoreIf closures
 */
class RecursiveInterpolationException extends RuntimeException {
  RecursiveInterpolationException() {
    super('Interpolation is made recursively')
  }
}
