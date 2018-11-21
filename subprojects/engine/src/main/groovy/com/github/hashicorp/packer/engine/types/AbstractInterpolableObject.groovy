package com.github.hashicorp.packer.engine.types

import groovy.transform.CompileStatic

@CompileStatic
abstract class AbstractInterpolableObject<ReadOnlyClass extends InterpolableObject, ReadWriteClass  extends InterpolableObject> implements InterpolableObject<ReadOnlyClass, ReadWriteClass> {
  @Override
  final boolean isReadOnly() {
    false
  }
}
