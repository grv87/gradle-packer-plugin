package com.github.hashicorp.packer.engine.enums

/**
 * What to do when the build fails
 */
enum OnError {
  /**
   * Clean up after the previous steps, deleting temporary files and virtual machines
   */
  CLEANUP,
  /**
   * Exit without any cleanup, which might require the next build to use {@code -force}
   */
  ABORT,
  /**
   * Present a prompt and wait for user to decide to clean up, abort, or retry the failed step
   */
  ASK
}
