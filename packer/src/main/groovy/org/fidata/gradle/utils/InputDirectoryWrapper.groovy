/*
 * InputDirectoryWrapper class
 * Copyright © 2018  Basil Peace
 */
package org.fidata.gradle.utils

import groovy.transform.CompileStatic
import groovy.transform.Internal
import org.gradle.api.tasks.InputDirectory
import org.gradle.api.tasks.PathSensitive
import org.gradle.api.tasks.PathSensitivity

/**
 * This class wraps single {@link File} instance
 * and marks it as {@link InputDirectory} for Gradle.
 *
 * This class is used as a workaround
 * for missing InputDirectories annotation
 */
@Internal
@CompileStatic
class InputDirectoryWrapper {
  private final File value

  /**
   * Gets actual {@link File} value
   * @return Actual value
   */
  @InputDirectory
  @PathSensitive(PathSensitivity.RELATIVE)
  File getValue() {
    this.@value
  }

  /**
   * Creates new InputDirectoryWrapper instance
   * @param value Actual {@link File} instance
   */
  InputDirectoryWrapper(File value) {
    this.value = value
  }
}
