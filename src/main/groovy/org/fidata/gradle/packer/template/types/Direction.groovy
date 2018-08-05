package org.fidata.gradle.packer.template.types

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonValue
import groovy.transform.CompileStatic

@CompileStatic
enum Direction {
  UPLOAD,
  DOWNLOAD

  @Override
  @JsonValue
  String toString(){
    this.name().toLowerCase()
  }

  @JsonCreator
  public static Direction forValue(String value) {
    if (value == value.toLowerCase()) {
      valueOf(value.toUpperCase())
    } else {
      // TODO: not found exception
    }
  }
}
