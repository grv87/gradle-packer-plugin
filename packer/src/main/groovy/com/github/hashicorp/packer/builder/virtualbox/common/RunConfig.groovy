package com.github.hashicorp.packer.builder.virtualbox.common

import org.fidata.packer.engine.annotations.AutoImplement
import org.fidata.packer.engine.annotations.LaunchedVMConfiguration
import org.fidata.packer.engine.types.InterpolableBoolean
import org.fidata.packer.engine.types.base.InterpolableObject
import org.fidata.packer.engine.types.InterpolableString
import org.fidata.packer.engine.types.InterpolableUnsignedInteger
import groovy.transform.CompileStatic

@AutoImplement
@CompileStatic
abstract class RunConfig implements InterpolableObject<RunConfig> {
  @LaunchedVMConfiguration // TOTEST
  abstract InterpolableBoolean getHeadless()

  @LaunchedVMConfiguration // TOTEST
  abstract InterpolableString getVrdpBindAddress()

  @LaunchedVMConfiguration // TOTEST
  abstract InterpolableUnsignedInteger getVrdpPortMin()

  @LaunchedVMConfiguration // TOTEST
  abstract InterpolableUnsignedInteger getVrdpPortMax()
}
