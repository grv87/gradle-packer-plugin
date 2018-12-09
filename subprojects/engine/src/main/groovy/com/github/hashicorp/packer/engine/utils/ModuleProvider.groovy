package com.github.hashicorp.packer.engine.utils

import com.fasterxml.jackson.databind.Module

interface ModuleProvider {
  Module getModule(Mutability mutability)
}
