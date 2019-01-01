package org.fidata.packer.engine.ast.valid.ignoreIf

import org.fidata.packer.engine.annotations.AutoImplement
import org.fidata.packer.engine.annotations.IgnoreIf
import org.fidata.packer.engine.annotations.OnlyIf
import org.fidata.packer.engine.types.InterpolableLong
import org.fidata.packer.engine.types.base.InterpolableObject
import groovy.transform.CompileStatic
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.Internal

// declaration
@AutoImplement
@CompileStatic
abstract class IgnoreIfTest implements InterpolableObject<IgnoreIfTest> {
  @Input
  abstract InterpolableLong getFirstField()

  @Input
  @OnlyIf({ -> firstField.interpolated == 0 })
  abstract InterpolableLong getSecondField()

  @Internal
  @IgnoreIf({ -> firstField.interpolated == 42 })
  abstract InterpolableLong getThirdField()
}
