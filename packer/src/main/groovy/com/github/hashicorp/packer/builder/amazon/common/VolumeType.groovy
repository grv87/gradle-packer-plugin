package com.github.hashicorp.packer.builder.amazon.common

import com.fasterxml.jackson.annotation.JsonValue

enum VolumeType {
  STANDARD,
  IO1,
  GP2,
  SC1,
  ST1

  @JsonValue
  @Override
  String toString() {
    this.name().toLowerCase()
  }
}
