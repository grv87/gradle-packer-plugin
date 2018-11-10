package com.github.hashicorp.packer.engine.types

import com.github.hashicorp.packer.engine.enums.ChecksumType
import groovy.transform.AutoClone
import groovy.transform.AutoCloneStyle
import groovy.transform.CompileStatic
import groovy.transform.InheritConstructors

@AutoClone(style = AutoCloneStyle.SIMPLE)
@InheritConstructors
@CompileStatic
class InterpolableChecksumType extends InterpolableEnum<ChecksumType> {
}
