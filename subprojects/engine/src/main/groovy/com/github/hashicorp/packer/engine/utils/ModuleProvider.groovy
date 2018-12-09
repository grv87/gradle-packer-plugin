package com.github.hashicorp.packer.engine.utils

import com.fasterxml.jackson.databind.Module
import groovy.transform.CompileStatic

@CompileStatic
interface ModuleProvider {
  Module getModule(Mutability mutability)
}
