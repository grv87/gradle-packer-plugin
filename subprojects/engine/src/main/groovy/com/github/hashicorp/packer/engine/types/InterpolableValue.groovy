package com.github.hashicorp.packer.engine.types

import groovy.transform.CompileDynamic
import groovy.transform.CompileStatic
import groovy.transform.EqualsAndHashCode
import groovy.transform.AutoClone
import groovy.transform.AutoCloneStyle
import com.fasterxml.jackson.annotation.JsonValue
import com.github.hashicorp.packer.engine.exceptions.InvalidRawValueClass

@AutoClone(style = AutoCloneStyle.SIMPLE)
// equals is required for Gradle up-to-date checking
@EqualsAndHashCode(includes = ['interpolatedValue'])
// @AutoExternalize(excludes = ['rawValue']) // TODO: Groovy 2.5.0
@CompileStatic
// Serializable and Externalizable are required for Gradle up-to-date checking
abstract class InterpolableValue<Source, Target extends Serializable> extends InterpolableObject implements Externalizable {
  @JsonValue
  Source rawValue

  // This constructor is required for Externalizable and AutoClone
  protected InterpolableValue() {
  }

  protected InterpolableValue(Source rawValue) {
    this.@rawValue = rawValue
  }

  private Target interpolatedValue

  Target getInterpolatedValue() throws IllegalStateException {
    if (!interpolated) {
      throw new IllegalStateException('Value is not interpolated yet')
    }
    this.interpolatedValue
  }

  @Override
  /*
   * CAVEAT:
   * We use dynamic compiling to run
   * overloaded version of doInterpolatePrimitive
   * depending on rawValue actual type
   */
  @CompileDynamic
  protected final void doInterpolate() {
    interpolatedValue = doInterpolatePrimitive(rawValue)
  }

  protected Target doInterpolatePrimitive(Object rawValue) {
    throw new InvalidRawValueClass(rawValue)
  }

  /* TODO protected Target doInterpolatePrimitive(Target rawValue) {
    this.@rawValue = rawValue
  }*/

  @SuppressWarnings('unused') // IDEA bug
  private static final long serialVersionUID = 7881876550613522317L

  void writeExternal(ObjectOutput out) throws IOException {
    out.writeObject(interpolatedValue)
  }

  void readExternal(ObjectInput oin) throws IOException, ClassNotFoundException {
    interpolatedValue = (Target)oin.readObject()
  }

  // This is used to create instances with default values
  static final InterpolableValue<Source, Target> forInterpolatedValue(Target interpolatedValue) {
    InterpolableValue<Source, Target> result = InterpolableValue.newInstance()
    // TODO
    result.@interpolated = true
    result.@interpolatedValue = interpolatedValue
    result
  }
}
