package com.github.hashicorp.packer.engine.types

import com.fasterxml.jackson.annotation.JsonCreator
import com.github.hashicorp.packer.engine.exceptions.InvalidRawValueClass
import groovy.transform.AutoClone
import groovy.transform.AutoCloneStyle
import groovy.transform.CompileStatic

@AutoClone(style = AutoCloneStyle.SIMPLE)
@CompileStatic
class InterpolableEnum<E extends Enum> extends InterpolableValue<Object, E> {
  final Class<E> enumClass

  protected InterpolableEnum(Class<E> enumClass) {
    this.enumClass = enumClass
  }

  InterpolableEnum(InterpolableString rawValue, Class<E> enumClass) {
    super(rawValue)
    this.enumClass = enumClass
  }

  @Override
  protected E doInterpolatePrimitive() {
    if (enumClass.isInstance(rawValue)) {
      (E)rawValue
    } else if (InterpolableString.isInstance(rawValue)) {
      ((InterpolableString)rawValue).interpolate context
      Enum.valueOf enumClass, ((InterpolableString) rawValue).interpolatedValue.toUpperCase()
    } else {
      throw new InvalidRawValueClass(rawValue)
    }
  }
}
