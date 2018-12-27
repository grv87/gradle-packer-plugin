package org.fidata.packer.engine.ast.valid.minimal

import org.fidata.packer.engine.annotations.AutoImplement
import org.fidata.packer.engine.annotations.Default
import org.fidata.packer.engine.types.InterpolableLong
import org.fidata.packer.engine.types.base.InterpolableObject
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
