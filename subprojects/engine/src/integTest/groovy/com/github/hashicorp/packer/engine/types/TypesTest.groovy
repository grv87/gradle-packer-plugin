package com.github.hashicorp.packer.engine.types

import com.github.hashicorp.packer.engine.Engine
import com.github.hashicorp.packer.engine.Mutability
import org.junit.Test

class TypesTest {
  @Test
  void testTypes() {
    InterpolableLong interpolableLong = new InterpolableLong.Raw(1L)
  }

  @Test
  void testEngine() {
    Engine engine = new Engine()
    InterpolableLong interpolableLong = engine.abstractTypeMappingRegistry.instantiate(InterpolableLong, Mutability.MUTABLE)
    assert InterpolableLong.Raw.isInstance(interpolableLong)
  }
}
