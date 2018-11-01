package com.github.hashicorp.packer.engine.types

import com.fasterxml.jackson.annotation.JsonCreator
import com.github.hashicorp.packer.engine.enums.ChecksumType

class InterpolableChecksumType extends InterpolableEnum<ChecksumType> {
  @JsonCreator
  InterpolableChecksumType(InterpolableString rawValue) {
    super(rawValue, ChecksumType)
  }
}
