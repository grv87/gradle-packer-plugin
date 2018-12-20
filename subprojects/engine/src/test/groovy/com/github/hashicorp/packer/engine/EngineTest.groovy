package com.github.hashicorp.packer.engine

import com.github.hashicorp.packer.engine.types.InterpolableString
import com.github.hashicorp.packer.engine.types.base.InterpolableObject
import org.junit.Test

class EngineTest {
  // Engine engine = new Engine()

  /*@Test
  void "registers InterpolableValue descendants in abstractTypeMapping"() {
    Engine engine = new Engine()
    assert engine.abstractTypeMappingRegistry[InterpolableString] != null
  }*/

  @Test
  void "is able to create new instances of abstract types"() {
    Engine engine = new Engine()
    InterpolableObject newInstance = engine.abstractTypeMappingRegistry.instantiate(InterpolableString, Mutability.MUTABLE)
    assert InterpolableString.Raw.isInstance(newInstance)
  }
}
