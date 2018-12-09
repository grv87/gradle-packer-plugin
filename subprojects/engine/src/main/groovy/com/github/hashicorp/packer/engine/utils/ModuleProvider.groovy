package com.github.hashicorp.packer.engine.utils

import com.fasterxml.jackson.databind.Module
import groovy.transform.CompileStatic

@CompileStatic
interface ModuleProvider {
  /**
   * Implementations should cache return value of this method
   * and, if configuration of a provider was not changed,
   * this method should return the same instance as the last call.
   *
   * If module's configuration is independent from mutability settings
   * then it is also recommended to return the same instance for any value of {@code mutability} argument.
   */
  Module getModule(Mutability mutability)
}
