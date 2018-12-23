package com.github.hashicorp.packer.engine.ast.erroneous.empty

import com.github.hashicorp.packer.engine.annotations.AutoImplement
import com.github.hashicorp.packer.engine.types.base.InterpolableObject
import groovy.transform.CompileStatic

@AutoImplement
@CompileStatic
abstract class Empty implements InterpolableObject<Empty> { }
