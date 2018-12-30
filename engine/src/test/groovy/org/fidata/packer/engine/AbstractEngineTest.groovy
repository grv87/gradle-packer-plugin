package org.fidata.packer.engine

import org.fidata.packer.engine.types.InterpolableString
import org.fidata.packer.engine.types.base.InterpolableObject
import org.junit.Test

class AbstractEngineTest {
  // AbstractEngine engine = new AbstractEngine()

  /*@Test
  void "registers InterpolableValue descendants in abstractTypeMapping"() {
    AbstractEngine engine = new AbstractEngine()
    assert engine.abstractTypeMappingRegistry[InterpolableString] != null
  }*/

  @Test
  void "is able to create new instances of abstract types"() {
    AbstractEngine engine = new AbstractEngine()
    InterpolableObject newInstance = engine.abstractTypeMappingRegistry.instantiate(InterpolableString, Mutability.MUTABLE)
    assert InterpolableString.Raw.isInstance(newInstance)
  }
}
