/*
 * InputDirectoryWrapper class
 * Copyright © 2018  Basil Peace
 */
package org.fidata.gradle.utils

import com.google.common.collect.ImmutableList
import groovy.transform.CompileStatic
import groovy.transform.Internal
import org.gradle.api.tasks.InputFile
import org.gradle.api.tasks.InputFiles
import org.gradle.api.tasks.Nested

/**
 * This class wraps a list of {@link File} instances
 * and marks them as {@link InputFile} for Gradle.
 *
 * This class is used as a workaround,
 * to overcome the problem that {@link InputFiles}
 * ignores order of the files by default,
 * and there is no built-in way to change this
 *
 * See https://github.com/gradle/gradle/issues/8132
 */
@Internal
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
