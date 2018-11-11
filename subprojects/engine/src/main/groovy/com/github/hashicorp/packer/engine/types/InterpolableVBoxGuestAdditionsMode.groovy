package com.github.hashicorp.packer.engine.types

import com.github.hashicorp.packer.engine.enums.VBoxGuestAdditionsMode
import groovy.transform.AutoClone
import groovy.transform.AutoCloneStyle
import groovy.transform.CompileStatic
import groovy.transform.InheritConstructors

@AutoClone(style = AutoCloneStyle.SIMPLE)
@InheritConstructors
@CompileStatic
class InterpolableVBoxGuestAdditionsMode extends InterpolableEnum<VBoxGuestAdditionsMode> {
  // This is used to create instances with default values
  static final InterpolableVBoxGuestAdditionsMode withDefault(VBoxGuestAdditionsMode interpolatedValue) {
    withDefault(InterpolableVBoxGuestAdditionsMode, interpolatedValue)
  }
}
