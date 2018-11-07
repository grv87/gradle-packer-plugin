package com.github.hashicorp.packer.engine.types

import com.fasterxml.jackson.annotation.JsonCreator
import com.github.hashicorp.packer.engine.enums.ChecksumType
import com.github.hashicorp.packer.engine.enums.VBoxGuestAdditionsMode
import groovy.transform.AutoClone
import groovy.transform.AutoCloneStyle
import groovy.transform.CompileStatic

@AutoClone(style = AutoCloneStyle.SIMPLE)
@CompileStatic
// @KnownImmutable // TODO: Groovy 2.5
class InterpolableVBoxGuestAdditionsMode extends InterpolableEnum<VBoxGuestAdditionsMode> {
  // This constructor is required for Externalizable
  protected InterpolableVBoxGuestAdditionsMode() {
    super(VBoxGuestAdditionsMode)
  }

  InterpolableVBoxGuestAdditionsMode(String rawValue) {
    super(rawValue, VBoxGuestAdditionsMode)
  }

  @JsonCreator
  InterpolableVBoxGuestAdditionsMode(VBoxGuestAdditionsMode rawValue) {
    super(rawValue, VBoxGuestAdditionsMode)
  }
}
