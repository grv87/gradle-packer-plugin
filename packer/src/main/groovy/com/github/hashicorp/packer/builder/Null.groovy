package com.github.hashicorp.packer.builder

import groovy.transform.CompileStatic
import com.github.hashicorp.packer.template.Builder
import org.fidata.packer.engine.AbstractEngine
import org.fidata.packer.engine.annotations.AutoImplement
import org.fidata.packer.engine.annotations.Inline
import com.github.hashicorp.packer.helper.Communicator

@AutoImplement
@CompileStatic
abstract class Null extends Builder<Null> {
  @Inline
  abstract Communicator getCommConfig()

  static void register(AbstractEngine engine) {
    engine.registerSubtype Builder, 'null', this
  }
}
