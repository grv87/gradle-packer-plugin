package com.github.hashicorp.packer.engine.types

import com.fasterxml.jackson.annotation.JsonCreator
import com.github.hashicorp.packer.engine.enums.ChecksumType
import groovy.transform.AutoClone
import groovy.transform.AutoCloneStyle
import groovy.transform.CompileStatic

@AutoClone(style = AutoCloneStyle.SIMPLE)
@CompileStatic
// @KnownImmutable // TODO: Groovy 2.5
class InterpolableChecksumType extends InterpolableEnum<ChecksumType> {
  // This constructor is required for Externalizable
  protected InterpolableChecksumType() {
    super(ChecksumType)
  }

  InterpolableChecksumType(ChecksumType rawValue) {
    super(rawValue, ChecksumType)
  }

  @JsonCreator
  InterpolableChecksumType(String rawValue) {
    super(rawValue, ChecksumType)
  }
}
