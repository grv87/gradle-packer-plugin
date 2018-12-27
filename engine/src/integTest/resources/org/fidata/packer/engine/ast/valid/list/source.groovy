package org.fidata.packer.engine.ast.valid.list

import org.fidata.packer.engine.annotations.AutoImplement
import org.fidata.packer.engine.types.InterpolableInteger
import org.fidata.packer.engine.types.base.InterpolableObject
import groovy.transform.CompileStatic
import org.gradle.api.tasks.Input

// declaration
@AutoImplement
@CompileStatic
abstract class ListTest implements InterpolableObject<ListTest> {
  @Input
  abstract List<InterpolableInteger> getSingleList()
}
