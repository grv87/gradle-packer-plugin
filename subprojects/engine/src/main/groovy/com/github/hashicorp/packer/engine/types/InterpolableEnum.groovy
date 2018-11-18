package com.github.hashicorp.packer.engine.types

import com.fasterxml.jackson.annotation.JsonCreator
import com.google.common.reflect.TypeToken
import groovy.transform.AutoClone
import groovy.transform.AutoCloneStyle
import groovy.transform.CompileStatic

@AutoClone(style = AutoCloneStyle.SIMPLE)
@CompileStatic
class InterpolableEnum<E extends Enum> extends InterpolableValue<Object, E> {
  @SuppressWarnings('UnstableApiUsage')
  static final Class<E> ENUM_CLASS = (Class<E>)new TypeToken<E>(this.class) { }.rawType

  // This constructor is required for Externalizable and AutoClone
  protected InterpolableEnum() {
  }

  InterpolableEnum(E rawValue) {
    super(rawValue)
  }

  private static Object tryCastStringToEnum(String rawValue) {
    String rawValueUpperCase = rawValue.toUpperCase()
    for (E enumConstant : ENUM_CLASS.enumConstants) {
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

  @SuppressWarnings("GrMethodMayBeStatic") // TOTEST
  protected final E doInterpolatePrimitive(E rawValue) {
    rawValue
  }

  protected final E doInterpolatePrimitive(InterpolableString rawValue) {
    Enum.valueOf ENUM_CLASS, rawValue.interpolatedValue(context).toUpperCase()
  }
}
