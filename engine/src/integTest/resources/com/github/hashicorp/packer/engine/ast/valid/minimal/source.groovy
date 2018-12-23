package com.github.hashicorp.packer.engine.ast.valid.minimal

import com.github.hashicorp.packer.engine.annotations.AutoImplement
import com.github.hashicorp.packer.engine.annotations.Default
import com.github.hashicorp.packer.engine.types.InterpolableLong
import com.github.hashicorp.packer.engine.types.base.InterpolableObject
import groovy.transform.CompileStatic
import org.gradle.api.tasks.Input

// declaration
@AutoImplement
@CompileStatic
abstract class MinimalTest implements InterpolableObject<MinimalTest> {
  @Input
  @Default({ 1L })
  abstract InterpolableLong getSingleField()
}
