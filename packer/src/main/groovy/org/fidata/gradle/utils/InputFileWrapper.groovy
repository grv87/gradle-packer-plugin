/*
 * InputDirectoryWrapper class
 * Copyright © 2018  Basil Peace
 */
package org.fidata.gradle.utils

import groovy.transform.CompileStatic
import org.gradle.api.tasks.InputDirectory
import org.gradle.api.tasks.InputFile
import org.gradle.api.tasks.PathSensitive
import org.gradle.api.tasks.PathSensitivity

/**
 * This class wraps single {@link File} instance
 * and marks it as {@link InputFile} for Gradle.
 *
 * TODOC: Usage cases
 */
@CompileStatic
class InputFileWrapper {
  private final File value

  /**
   * Gets actual {@link File} value
   * @return Actual value
   */
  @InputFile
  @PathSensitive(PathSensitivity.NONE)
  File getValue() {
    this.@value
  }

  /**
   * Creates new InputFileWrapper instance
   * @param value Actual {@link File} instance
   */
  InputFileWrapper(File value) {
    this.value = value
  }
}
