package com.github.hashicorp.packer.engine.types

import groovy.transform.CompileStatic

@CompileStatic
abstract class AbstractInterpolableReadOnlyObject<ReadOnlyClass extends AbstractInterpolableReadOnlyObject, ReadWriteClass  extends InterpolableObject> implements InterpolableObject<ReadOnlyClass, ReadWriteClass> {
  @Override
  final boolean isReadOnly() {
    true
  }

  @Override
  final ReadOnlyClass asReadOnly() {
    (ReadOnlyClass)this
  }
}
