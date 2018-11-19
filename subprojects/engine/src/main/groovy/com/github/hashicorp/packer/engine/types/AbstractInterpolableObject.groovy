package com.github.hashicorp.packer.engine.types

import groovy.transform.CompileStatic

@CompileStatic
abstract class AbstractInterpolableObject<ThisClass extends AbstractInterpolableObject> implements InterpolableObject<ThisClass> {
  private final boolean readOnly

  final boolean isReadOnly() {
    this.@readOnly
  }

  final ThisClass asReadOnly() {
    this.@readOnly ? (ThisClass)this : asReadOnly
  }

  abstract protected ThisClass getAsReadOnly()

  // these constructors are made public
  // so they can be easily inherited
  AbstractInterpolableObject() {
    this(false)
  }

  AbstractInterpolableObject(boolean readOnly) {
    this.@readOnly = readOnly
  }
}
