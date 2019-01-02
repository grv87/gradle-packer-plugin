package com.github.hashicorp.packer.enums

import com.fasterxml.jackson.annotation.JsonValue
import groovy.transform.CompileStatic

@CompileStatic
enum ChecksumType {
  NONE,
  MD5,
  SHA1,
  SHA256,
  SHA512

  @JsonValue
  @Override
  String toString() {
    // TOTHINK: cache result ?
    this.name().toLowerCase()
  }
}
