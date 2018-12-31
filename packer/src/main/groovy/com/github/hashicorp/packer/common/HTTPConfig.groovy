package com.github.hashicorp.packer.common

import org.fidata.packer.engine.annotations.AutoImplement
import org.fidata.packer.engine.annotations.ConnectionSetting
import org.fidata.packer.engine.types.InterpolableFile
import org.fidata.packer.engine.types.base.InterpolableObject
import org.fidata.packer.engine.types.InterpolableUnsignedInteger
import groovy.transform.CompileStatic
import org.gradle.api.tasks.InputDirectory
import org.gradle.api.tasks.Optional
import org.gradle.api.tasks.PathSensitive
import org.gradle.api.tasks.PathSensitivity

@AutoImplement
@CompileStatic
abstract class HTTPConfig implements InterpolableObject<HTTPConfig> {
  @InputDirectory
  @PathSensitive(PathSensitivity.RELATIVE)
  @Optional
  abstract InterpolableFile getHttpDir()

  @ConnectionSetting
  abstract InterpolableUnsignedInteger getHttpPortMin()

  @ConnectionSetting
  abstract InterpolableUnsignedInteger getHttpPortMax()
}
