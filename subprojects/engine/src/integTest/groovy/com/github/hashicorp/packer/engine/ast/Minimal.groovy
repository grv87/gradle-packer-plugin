package com.github.hashicorp.packer.engine.ast

import com.github.hashicorp.packer.engine.annotations.AutoImplement
import com.github.hashicorp.packer.engine.annotations.Default
import com.github.hashicorp.packer.engine.types.InterpolableLong
import com.github.hashicorp.packer.engine.types.InterpolableObject
import groovy.transform.CompileStatic
import org.gradle.api.tasks.Input

// declaration
@AutoImplement
@CompileStatic
abstract class Minimal implements InterpolableObject<Minimal> {
  @Input
  @Default({ 1L })
  abstract InterpolableLong getSingleField()
}