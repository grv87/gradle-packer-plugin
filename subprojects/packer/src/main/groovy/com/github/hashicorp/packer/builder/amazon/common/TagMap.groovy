package com.github.hashicorp.packer.builder.amazon.common

import com.fasterxml.jackson.annotation.JsonCreator
import com.github.hashicorp.packer.engine.types.InterpolableString
import com.github.hashicorp.packer.engine.types.InterpolableValue
import groovy.transform.AutoClone
import groovy.transform.AutoCloneStyle
import groovy.transform.CompileStatic

@AutoClone(style = AutoCloneStyle.SIMPLE)
@CompileStatic
class TagMap extends InterpolableValue<Map<InterpolableString, InterpolableString>, HashMap<String, String>> {
  static final class MapClass extends HashMap<InterpolableString, InterpolableString> {
  }

  @JsonCreator
  TagMap(MapClass rawValue) {
    super(rawValue)
  }

  protected static /* TOTEST */ HashMap<String, String> doInterpolatePrimitive(MapClass rawValue) {
    new HashMap((Map<String, String>)rawValue.collectEntries { HashMap.Entry<InterpolableString, InterpolableString> entry ->
      [entry.key.interpolatedValue(context), entry.value.interpolatedValue(context)]
    })
  }
}
