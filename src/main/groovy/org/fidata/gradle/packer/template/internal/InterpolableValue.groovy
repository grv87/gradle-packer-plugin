package org.fidata.gradle.packer.template.internal

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonValue
import groovy.transform.CompileStatic
import org.fidata.gradle.packer.template.Context
import org.gradle.api.tasks.Internal

@CompileStatic
abstract class InterpolableValue<Source, Target extends Serializable> extends InterpolableObject {
  @JsonValue
  @Internal
  Source rawValue

  @JsonCreator
  InterpolableValue(Source rawValue) {
    this.rawValue = rawValue
  }

  private Target interpolatedValue

  Target getInterpolatedValue() {
    this.interpolatedValue
  }

  @Override
  protected final void doInterpolate() {
    interpolatedValue = doInterpolatePrimitive()
  }

  abstract protected Target doInterpolatePrimitive()

  @Override
  boolean equals(Object obj) {
    this.class.isInstance(obj) && ((InterpolableValue<Source, Target>)obj).interpolatedValue == interpolatedValue
  }

  @Override
  int hashCode() {
    interpolatedValue.hashCode()
  }

  private static final long serialVersionUID = 1L

  /*private void writeObject(ObjectOutputStream out) throws IOException {
    out.writeChars(interpolatedValue)
  }*/
  private Object writeReplace() throws ObjectStreamException {
    interpolatedValue
  }

}
