package org.fidata.packer.engine.types

import org.fidata.packer.engine.AbstractEngine
import org.fidata.packer.engine.Mutability
import com.github.hashicorp.packer.template.Context
import org.apache.commons.lang3.SerializationUtils
import org.junit.Test

class TypesTest {
  @Test
  void testTypes() {
    InterpolableLong interpolableLong = new InterpolableLong.Raw(new Random().nextLong())
  }

  @Test
  void testEngine() {
    AbstractEngine engine = new AbstractEngine()
    InterpolableLong interpolableLong = engine.abstractTypeMappingRegistry.instantiate(InterpolableLong, Mutability.MUTABLE)
    assert InterpolableLong.Raw.isInstance(interpolableLong)
  }

  @Test
  void testSerialization() {
    /*
     * CAVEAT:
     * SerializationUtils.clone assumes:
     * > the deserialized object is of the same type as the original serialized object
     * This is not in our case.
     * But it works, at least as long as we treat interpolated as interface, not as Interpolated instance
     */
    InterpolableLong raw = new InterpolableLong.Raw(new Random().nextLong())
    InterpolableLong interpolated = raw.interpolateValue((Context)null)
    assert Serializable.isInstance(interpolated)
    Object deserialized = SerializationUtils.clone(interpolated)
    assert InterpolableLong.AlreadyInterpolated.isInstance(deserialized)
    assert interpolated == deserialized
  }
}
