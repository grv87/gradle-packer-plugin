package com.github.hashicorp.packer.engine.ast.valid.list

import com.github.hashicorp.packer.engine.annotations.AutoImplement
import com.github.hashicorp.packer.engine.types.InterpolableInteger
import com.github.hashicorp.packer.engine.types.base.InterpolableObject
import groovy.transform.CompileStatic
import org.gradle.api.tasks.Input

// declaration
@AutoImplement
@CompileStatic
abstract class ListTest implements InterpolableObject<ListTest> {
  @Input
  abstract List<InterpolableInteger> getSingleList()
}
