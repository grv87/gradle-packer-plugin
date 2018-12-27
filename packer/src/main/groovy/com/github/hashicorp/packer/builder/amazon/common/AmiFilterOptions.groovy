package com.github.hashicorp.packer.builder.amazon.common

import org.fidata.packer.engine.annotations.AutoImplement
import org.fidata.packer.engine.types.InterpolableBoolean
import org.fidata.packer.engine.types.base.InterpolableObject
import org.fidata.packer.engine.types.InterpolableString
import org.fidata.packer.engine.types.InterpolableStringArray
import groovy.transform.CompileStatic

@AutoImplement
@CompileStatic
abstract class AmiFilterOptions implements InterpolableObject<AmiFilterOptions> {
  abstract Map<InterpolableString, InterpolableString> getFilters()
  abstract InterpolableStringArray getOwners()
  abstract InterpolableBoolean getMostRecent()
}
