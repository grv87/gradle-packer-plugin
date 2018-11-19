package com.github.hashicorp.packer.engine.types

import groovy.transform.CompileStatic
import com.github.hashicorp.packer.template.Context

import java.util.function.Function

@CompileStatic
interface InterpolableObject<ThisClass extends InterpolableObject> /*extends Cloneable*/ {
  ThisClass interpolate(Context context)
}
