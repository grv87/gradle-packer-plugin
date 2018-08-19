package org.fidata.gradle.packer.template.enums

import groovy.transform.CompileStatic
import com.fasterxml.jackson.annotation.JsonValue
import com.fasterxml.jackson.annotation.JsonCreator
import java.util.zip.DataFormatException

@CompileStatic
enum Direction {
  UPLOAD,
  DOWNLOAD

  @JsonValue
  @Override
  String toString() {
    this.name().toLowerCase()
  }

  @JsonCreator
  static Direction forValue(String value) throws DataFormatException {
    if (value == value.toLowerCase()) {
      valueOf(value.toUpperCase())
    } else {
      throw new DataFormatException(sprintf('%s if not a valid direction value', [value])) // TODO
    }
  }
}
