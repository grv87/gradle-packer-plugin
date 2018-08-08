package org.fidata.gradle.packer.template.types

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonValue
import groovy.transform.CompileStatic
import java.util.zip.DataFormatException

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
  public static Direction forValue(String value) throws DataFormatException {
    if (value == value.toLowerCase()) {
      valueOf(value.toUpperCase())
    } else {
      throw new DataFormatException(sprintf('%s if not a valid direction value', [value])) // TODO
    }
  }
}
