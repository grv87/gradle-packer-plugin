package com.github.hashicorp.packer.engine.ast.valid.ignoreIf

import com.github.hashicorp.packer.engine.annotations.AutoImplement
import com.github.hashicorp.packer.engine.annotations.IgnoreIf
import com.github.hashicorp.packer.engine.types.InterpolableLong
import com.github.hashicorp.packer.engine.types.base.InterpolableObject
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
  @IgnoreIf({ -> firstField.interpolated != 0 })
  abstract InterpolableLong getSecondField()

  @Internal
  @IgnoreIf({ -> firstField.interpolated == 42 })
  abstract InterpolableLong getThirdField()
}
