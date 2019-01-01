/*
 * InputDirectoryWrapper class
 * Copyright © 2018  Basil Peace
 */
package org.fidata.gradle.utils

import com.google.common.collect.ImmutableList
import groovy.transform.CompileStatic
import org.gradle.api.tasks.InputFile
import org.gradle.api.tasks.Nested
import org.gradle.api.tasks.PathSensitive
import org.gradle.api.tasks.PathSensitivity

/**
 * This class wraps single {@link File} instance
 * and marks it as {@link InputFile} for Gradle.
 *
 * TODOC: Usage cases
 */
@CompileStatic
class OrderedInputFilesWrapper {
  private final List<InputFileWrapper> values

  /**
   * Gets list of wrapped values
   * @return List of wrapped values
   */
  @Nested
  List<InputFileWrapper> getValues() {
    this.@values
  }

  /**
   * Creates new OrderedInputFilesWrapper instance
   * @param values Actual list of {@link File} instances
   */
  OrderedInputFilesWrapper(List<File> values) {
    this.values = ImmutableList.copyOf(values.collect { File file -> new InputFileWrapper(file) })
  }
}
