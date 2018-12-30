package org.fidata.packer.engine

import com.github.hashicorp.packer.template.Context
import org.fidata.packer.engine.types.base.InterpolableObject

class SubtypeRegistryTest {
  private class InterpolableObjectMock implements InterpolableObject<InterpolableObjectMock> {
    @Override
    InterpolableObjectMock interpolate(Context context) {
      this
    }
  }

  void testModuleName() {
    SubtypeRegistry<InterpolableObjectMock> subtypeRegistry = SubtypeRegistry.forType(InterpolableObjectMock)
    assert subtypeRegistry.moduleName == 'org.fidata.packer.engine.SubtypeRegistry<org.fidata.packer.engine.SubtypeRegistryTest.InterpolableObjectMock>'
  }
}
