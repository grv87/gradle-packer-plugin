package com.github.hashicorp.packer.engine.types

import com.fasterxml.jackson.annotation.JsonCreator
import com.github.hashicorp.packer.engine.exceptions.InvalidRawValueClass
import com.google.common.reflect.TypeToken
import groovy.transform.AutoClone
import groovy.transform.AutoCloneStyle
import groovy.transform.CompileStatic

@AutoClone(style = AutoCloneStyle.SIMPLE)
@CompileStatic
// @KnownImmutable // TODO: Groovy 2.5
class InterpolableEnum<E extends Enum> extends InterpolableValue<Object, E> {
  @SuppressWarnings("UnstableApiUsage")
  static final Class<E> enumClass = (Class<E>)new TypeToken<E>(this.class) { }.rawType

  // This constructor is required for Externalizable and AutoClone
  protected InterpolableEnum() {
    super()
  }

  InterpolableEnum(E rawValue) {
    super(rawValue)
  }

  private static Object tryCastStringToEnum(String rawValue) {
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

  protected final E doInterpolatePrimitive(E rawValue) {
    rawValue
  }

  protected final E doInterpolatePrimitive(InterpolableString rawValue) {
    rawValue.interpolate context
    Enum.valueOf enumClass, rawValue.interpolatedValue.toUpperCase()
  }
}
