package org.fidata.gradle.packer.template.internal

import groovy.transform.CompileStatic
import org.fidata.gradle.packer.template.Context

@CompileStatic
abstract class InterpolablePrimitive<T> extends InterpolableObject {
  private T interpolatedValue

  T getInterpolatedValue() {
    this.interpolatedValue
  }

  @Override
  protected final void doInterpolate(Context ctx) {
    interpolatedValue = doInterpolatePrimitive(ctx)
  }

  abstract protected T doInterpolatePrimitive(Context ctx)

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
