package org.fidata.gradle.packer.template.internal

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonValue
import groovy.transform.CompileStatic
import org.fidata.gradle.packer.template.Context

@CompileStatic
abstract class InterpolablePrimitive<Target> extends InterpolableObject {
  private Target interpolatedValue

  Target getInterpolatedValue() {
    this.interpolatedValue
  }

  @Override
  protected final void doInterpolate(Context ctx) {
    interpolatedValue = doInterpolatePrimitive(ctx)
  }

  abstract protected Target doInterpolatePrimitive(Context ctx)

  @Override
  boolean equals(Object obj) {
    this.class.isInstance(obj) && ((InterpolablePrimitive)obj).interpolatedValue == interpolatedValue
  }

  private static final long serialVersionUID = 1L

  /*private void writeObject(ObjectOutputStream out) throws IOException {
    out.writeChars(interpolatedValue)
  }*/
  private Object writeReplace() throws ObjectStreamException {
    interpolatedValue
  }
}
