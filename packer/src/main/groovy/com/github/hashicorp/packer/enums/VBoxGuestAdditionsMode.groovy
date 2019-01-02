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
    // TOTHINK: cache result ?
    this.name().toLowerCase()
  }
}
