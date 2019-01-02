package org.fidata.packer.engine.types.base

import groovy.transform.CompileStatic
import com.github.hashicorp.packer.template.Context

@CompileStatic
interface InterpolableObject<ThisClass extends InterpolableObject> {
  /**
   *
   * @param context
   * @throws UnsupportedOperationException
   * @return
   */
  ThisClass interpolate(Context context)
}
