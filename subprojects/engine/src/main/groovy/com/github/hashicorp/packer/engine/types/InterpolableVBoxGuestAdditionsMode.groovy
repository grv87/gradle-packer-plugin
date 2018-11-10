package com.github.hashicorp.packer.engine.types

import com.github.hashicorp.packer.engine.enums.VBoxGuestAdditionsMode
import groovy.transform.AutoClone
import groovy.transform.AutoCloneStyle
import groovy.transform.CompileStatic
import groovy.transform.InheritConstructors

@AutoClone(style = AutoCloneStyle.SIMPLE)
@InheritConstructors
// @KnownImmutable // TODO: Groovy 2.5
@CompileStatic
class InterpolableVBoxGuestAdditionsMode extends InterpolableEnum<VBoxGuestAdditionsMode> {
}
