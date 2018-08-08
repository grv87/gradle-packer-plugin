package org.fidata.gradle.packer.template.types

import groovy.transform.CompileStatic
import org.fidata.gradle.packer.template.Context
import org.fidata.gradle.packer.template.internal.InterpolableObject

@CompileStatic
class InterpolableFile extends InterpolableObject {
  String value

  private File interpolatedValue

  File getInterpolatedValue() {
    interpolatedValue
  }

  @Override
  protected void doInterpolate(Context ctx) {
    interpolatedValue = ctx.interpolateFile(value)
  }

  @Override
  boolean equals(Object obj) {
    this.class.isInstance(obj) && ((InterpolableFile)obj).interpolatedValue == interpolatedValue
  }

  private static final long serialVersionUID = 1L

  /*private void writeObject(ObjectOutputStream out) throws IOException {
    out.writeChars(interpolatedValue)
  }*/
  private Object writeReplace() throws ObjectStreamException {
    interpolatedValue
  }

  /* TOTEST InterpolableFile() {
  }*/
}
