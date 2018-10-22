package com.github.hashicorp.packer.engine.types

import groovy.transform.EqualsAndHashCode
import groovy.transform.AutoClone
import groovy.transform.AutoCloneStyle
import groovy.transform.CompileStatic
import com.fasterxml.jackson.annotation.JsonValue
import org.gradle.api.tasks.Internal
import com.fasterxml.jackson.annotation.JsonCreator

@EqualsAndHashCode(includes = ['interpolatedValue'])
@AutoClone(style = AutoCloneStyle.SIMPLE, excludes = ['interpolatedValue'])
@CompileStatic
abstract class InterpolableValue<Source, Target extends Serializable> extends InterpolableObject implements Serializable {
  @JsonValue
  @Internal
  Source rawValue

  protected InterpolableValue() {
  }

  @JsonCreator
  protected /* TOTEST */ InterpolableValue(Source rawValue) {
    this.rawValue = rawValue
  }

  private Target interpolatedValue

  Target getInterpolatedValue() throws IllegalStateException {
    if (!interpolated) {
      throw new IllegalStateException('Value is not interpolated yet')
    }
    this.interpolatedValue
  }

  @Override
  protected final void doInterpolate() {
    interpolatedValue = doInterpolatePrimitive()
  }

  abstract protected Target doInterpolatePrimitive()

  private static final long serialVersionUID = 1L

  /*private void writeObject(ObjectOutputStream out) throws IOException {
    out.writeChars(interpolatedValue)
  }*/
  /*
   * WORKAROUND:
   * Bug in CodeNarc
   * <grv87 2018-08-19>
   */
  @SuppressWarnings('UnusedPrivateMethod')
  private Object writeReplace() throws ObjectStreamException {
    interpolatedValue
  }

}
