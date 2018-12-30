package org.fidata.packer.engine.ast.valid.customRegister

import org.fidata.packer.engine.AbstractEngine
import org.fidata.packer.engine.annotations.AutoImplement
import org.fidata.packer.engine.annotations.Default
import org.fidata.packer.engine.types.InterpolableLong
import org.fidata.packer.engine.types.base.InterpolableObject
import groovy.transform.CompileStatic
import org.gradle.api.tasks.Input

// declaration
@AutoImplement
@CompileStatic
abstract class CustomRegisterTest implements InterpolableObject<CustomRegisterTest> {
  @Input
  @Default({ 1L })
  abstract InterpolableLong getSingleField()

  static final void register(AbstractEngine e) {
    // do some stuff
    new Random().nextInt()
  }
}
