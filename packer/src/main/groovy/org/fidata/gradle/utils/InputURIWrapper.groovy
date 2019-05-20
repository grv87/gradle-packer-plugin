/*
 * InputDirectoryWrapper class
 * Copyright ©  Basil Peace
 */
package org.fidata.gradle.utils

import groovy.transform.CompileStatic
import groovy.transform.Internal
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.InputFile
import org.gradle.api.tasks.Optional
import org.gradle.api.tasks.PathSensitive
import org.gradle.api.tasks.PathSensitivity

/**
 * This class wraps {@link URI} instance
 * and marks it as {@link InputFile} (if URI points to local file)
 * or {@link Input} (if URI points to remote) for Gradle.
 *
 * This class is used as a workaround,
 * to overcome the problem that Gradle can't handle
 * remote URIs/URLs as input files
 */
@Internal
@CompileStatic
class InputURIWrapper {
  private final URI value

  /**
   * Gets actual {@link URI} value, if it is pointed to local file
   * @return Actual value
   */
  @InputFile
  @PathSensitive(PathSensitivity.NONE)
  @Optional
  URI getLocalURI() { // TODO: RegularFile ?
    /*
     * TOTEST:
     * WORKAROUND:
     * Without equals we got compilation error:
     * [Static type checking] - Cannot find matching method org.codehaus.groovy.runtime.ScriptBytecodeAdapter#compareEquals(java.lang.String, java.lang.String). Please check if the declared type is correct and if the method exists.
     * <grv87 2018-12-09>
     */
    'file'.equals(value.scheme) ? value : null
  }

  @Input
  @Optional
  URI getRemoteURI() {
    /*
     * TOTEST:
     * WORKAROUND:
     * Without equals we got compilation error:
     * [Static type checking] - Cannot find matching method org.codehaus.groovy.runtime.ScriptBytecodeAdapter#compareEquals(java.lang.String, java.lang.String). Please check if the declared type is correct and if the method exists.
     * <grv87 2018-12-09>
     */
    'file'.equals(value.scheme) ? value : null
  }

  /**
   * Creates new InputURIWrapper instance
   * @param value Actual {@link URI} instance
   */
  InputURIWrapper(URI value) {
    this.value = value
  }
}
