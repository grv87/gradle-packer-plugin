package com.github.hashicorp.packer.builder.amazon.common

import com.fasterxml.jackson.annotation.JsonCreator
import com.github.hashicorp.packer.engine.types.InterpolableString
import com.github.hashicorp.packer.engine.types.base.InterpolableValue
import groovy.transform.CompileStatic

@CompileStatic
class TagMap extends InterpolableValue<Map<InterpolableString, InterpolableString>, HashMap<String, String>> {
  static final class MapClass extends HashMap<InterpolableString, InterpolableString> {
  }

  @JsonCreator(mode = JsonCreator.Mode.DELEGATING)
  TagMap(MapClass rawValue) {
    super(rawValue)
  }

  protected static /* TOTEST */ HashMap<String, String> doInterpolatePrimitive(MapClass rawValue) {
    new HashMap((Map<String, String>)rawValue.collectEntries { InterpolableString key, InterpolableString value ->
      [key.interpolatedValue(context), value.interpolatedValue(context)]
    })
  }
}
