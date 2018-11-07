package com.github.hashicorp.packer.engine.types

import com.fasterxml.jackson.annotation.JsonCreator
import com.github.hashicorp.packer.engine.exceptions.InvalidRawValueClass
import groovy.transform.AutoClone
import groovy.transform.AutoCloneStyle
import groovy.transform.CompileStatic

@AutoClone(style = AutoCloneStyle.SIMPLE)
@CompileStatic
// @KnownImmutable // TODO: Groovy 2.5
class InterpolableEnum<E extends Enum> extends InterpolableValue<Object, E> {
  final Class<E> enumClass

  // This constructor is required for Externalizable
  protected InterpolableEnum(Class<E> enumClass) {
    super()
    this.enumClass = enumClass
  }

  protected InterpolableEnum(E rawValue, Class<E> enumClass) {
    super(rawValue)
    this.enumClass = enumClass
  }

  private static final Object tryCastStringToEnum(String rawValue, Class<E> enumClass) {
    String rawValueUpperCase = rawValue.toUpperCase()
    for (E enumConstant : enumClass.enumConstants) {
      if (enumConstant.name() == rawValueUpperCase) {
        return enumConstant
      }
    }
    return new InterpolableString(rawValue)
  }

  protected InterpolableEnum(String rawValue, Class<E> enumClass) {
    super(tryCastStringToEnum(rawValue, enumClass))
    this.enumClass = enumClass
  }

  @Override
  protected E doInterpolatePrimitive() {
    if (enumClass.isInstance(rawValue)) {
      (E)rawValue
    } else if (InterpolableString.isInstance(rawValue)) {
      InterpolableString rawInterpolableString = (InterpolableString)rawValue
      rawInterpolableString.interpolate context
      Enum.valueOf enumClass, rawInterpolableString.interpolatedValue.toUpperCase()
    } else {
      throw new InvalidRawValueClass(rawValue)
    }
  }
}
