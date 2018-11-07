package com.github.hashicorp.packer.engine.enums

import groovy.transform.CompileStatic

/**
 * This key determines what to do when a normal multistep step fails
 * What to do when the build fails
 */
@CompileStatic
enum OnError {
  /**
   * Run cleanup steps
   * Clean up after the previous steps, deleting temporary files and virtual machines
   */
  CLEANUP,
  /**
   * Exit without cleanup
   * Exit without any cleanup, which might require the next build to use {@code -force}
   */
  ABORT,
  /**
   * Ask the user
   * Present a prompt and wait for user to decide to clean up, abort, or retry the failed step
   */
  ASK

  @Override
  String toString() {
    this.name().toLowerCase()
  }
}
