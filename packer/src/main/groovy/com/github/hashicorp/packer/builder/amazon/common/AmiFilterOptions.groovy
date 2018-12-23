package com.github.hashicorp.packer.builder.amazon.common

import com.github.hashicorp.packer.engine.annotations.AutoImplement
import com.github.hashicorp.packer.engine.types.InterpolableBoolean
import com.github.hashicorp.packer.engine.types.base.InterpolableObject
import com.github.hashicorp.packer.engine.types.InterpolableString
import com.github.hashicorp.packer.engine.types.InterpolableStringArray
import groovy.transform.CompileStatic

@AutoImplement
@CompileStatic
abstract class AmiFilterOptions implements InterpolableObject<AmiFilterOptions> {
  abstract Map<InterpolableString, InterpolableString> getFilters()
  abstract InterpolableStringArray getOwners()
  abstract InterpolableBoolean getMostRecent()
}
