package com.github.hashicorp.packer.engine.types

import com.github.hashicorp.packer.template.Context
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
  Source rawValue = null

  // This constructor is required for Externalizable and AutoClone
  protected InterpolableValue() {
  }

  protected InterpolableValue(Source rawValue) {
    this.@rawValue = rawValue
  }

  /**
   * @serial Interpolated value
   */
  private Target interpolatedValue

  Target getInterpolatedValue() throws IllegalStateException {
    if (!interpolated) {
      throw new IllegalStateException('Value is not interpolated yet')
    }
    this.interpolatedValue
  }

  final Target interpolatedValue(Context context) {
    interpolate(context)
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
    if (!isDefault || rawValue != null /* Means that somebody reassigned value to custom */) {
      interpolatedValue = doInterpolatePrimitive(rawValue)
      isDefault = false
    }
  }

  protected static /* TOTEST */ Target doInterpolatePrimitive(Object rawValue) {
    throw new InvalidRawValueClass(rawValue)
  }

  /* TODO protected Target doInterpolatePrimitive(Target rawValue) {
    this.@rawValue = rawValue
  }*/

  @SuppressWarnings('unused') // IDEA bug
  private static final long serialVersionUID = 7881876550613522317L

  /**
   * @serialData interpolatedValue
   */
  @Override
  void writeExternal(ObjectOutput out) throws IOException {
    out.writeObject(interpolatedValue)
  }

  @Override
  void readExternal(ObjectInput oin) throws IOException, ClassNotFoundException {
    interpolatedValue = (Target)oin.readObject()
    interpolated = true // TODO: use isDefault/isInterpolatedWithoutContext ?
  }

  private boolean isDefault = false

  // This is used to create instances with default values
  protected static final <V extends InterpolableValue<Source, Target>> V withDefault(Class<V> clazz, Target interpolatedValue) {
    V result =  (V)clazz.newInstance() /* TODO */
    // TODO
    result.@interpolatedValue = interpolatedValue
    result.@isDefault = true
    result
  }
}
