package com.github.hashicorp.packer.enums

import com.fasterxml.jackson.annotation.JsonValue
import groovy.transform.CompileStatic

@CompileStatic
enum VBoxGuestAdditionsMode {
  UPLOAD,
  ATTACH,
  DISABLE

  @JsonValue
  @Override
  String toString() {
    this.name().toLowerCase()
  }
}
