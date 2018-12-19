package com.github.hashicorp.packer.builder.amazon.common

import com.github.hashicorp.packer.engine.annotations.AutoImplement
import com.github.hashicorp.packer.engine.types.InterpolableBoolean
import com.github.hashicorp.packer.engine.types.base.InterpolableObject
import com.github.hashicorp.packer.engine.types.InterpolableString
import com.github.hashicorp.packer.engine.types.InterpolableStringArray
import groovy.transform.CompileStatic

@AutoImplement
@CompileStatic
interface AmiFilterOptions extends InterpolableObject {
  Map<InterpolableString, InterpolableString> filters
  InterpolableStringArray owners
  InterpolableBoolean mostRecent
}
