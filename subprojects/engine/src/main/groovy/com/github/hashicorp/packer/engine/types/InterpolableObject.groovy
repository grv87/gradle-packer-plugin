package com.github.hashicorp.packer.engine.types

import groovy.transform.CompileStatic
import com.github.hashicorp.packer.template.Context

@CompileStatic
interface InterpolableObject<ReadOnlyClass extends InterpolableObject, ReadWriteClass  extends InterpolableObject> {
  /**
   *
   * @param context
   * @throws UnsupportedOperationException
   * @return
   */
  ReadOnlyClass interpolate(Context context)

  boolean isReadOnly()

  ReadOnlyClass asReadOnly()

  ReadWriteClass asReadWrite()
}
