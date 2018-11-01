package com.github.hashicorp.packer.engine.types

import com.fasterxml.jackson.annotation.JsonCreator
import com.github.hashicorp.packer.engine.enums.ChecksumType
import com.github.hashicorp.packer.engine.enums.VBoxGuestAdditionsMode

class InterpolableVBoxGuestAdditionsMode extends InterpolableEnum<VBoxGuestAdditionsMode> {
  @JsonCreator
  InterpolableVBoxGuestAdditionsMode(InterpolableString rawValue) {
    super(rawValue, VBoxGuestAdditionsMode)
  }
}
