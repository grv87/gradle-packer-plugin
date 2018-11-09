package com.github.hashicorp.packer.engine.types

import com.fasterxml.jackson.annotation.JsonCreator
import com.github.hashicorp.packer.engine.exceptions.InvalidRawValueClass
import groovy.transform.AutoClone
import groovy.transform.AutoCloneStyle
import groovy.transform.CompileStatic
import java.lang.reflect.ParameterizedType

@AutoClone(style = AutoCloneStyle.SIMPLE)
@CompileStatic
// @KnownImmutable // TODO: Groovy 2.5
class InterpolableEnum<E extends Enum> extends InterpolableValue<Object, E> {
  static final Class<E> enumClass = (Class<E>)((ParameterizedType)this.genericSuperclass).actualTypeArguments[0]

  // This constructor is required for Externalizable and AutoClone
  protected InterpolableEnum() {
    super()
  }

  InterpolableEnum(E rawValue) {
    super(rawValue)
  }

  private static final Object tryCastStringToEnum(String rawValue) {
    String rawValueUpperCase = rawValue.toUpperCase()
    for (E enumConstant : enumClass.enumConstants) {
      if (enumConstant.name() == rawValueUpperCase) {
        return enumConstant
      }
    }
    return new InterpolableString(rawValue)
  }

  @JsonCreator
  InterpolableEnum(String rawValue) {
    super(tryCastStringToEnum(rawValue))
  }

  @Override
  protected final E doInterpolatePrimitive() {
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
