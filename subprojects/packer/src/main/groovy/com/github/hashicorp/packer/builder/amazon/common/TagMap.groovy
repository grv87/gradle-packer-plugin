package com.github.hashicorp.packer.builder.amazon.common

import com.github.hashicorp.packer.engine.types.InterpolableString
import groovy.transform.AutoClone
import groovy.transform.AutoCloneStyle
import groovy.transform.CompileStatic

@AutoClone(style = AutoCloneStyle.SIMPLE)
@CompileStatic
class TagMap extends HashMap<InterpolableString, InterpolableString> {
}
