package com.github.hashicorp.packer.engine.types

import com.fasterxml.jackson.annotation.JsonCreator
import com.github.hashicorp.packer.template.Context
import com.google.common.reflect.TypeToken
import groovy.transform.CompileStatic
import groovy.transform.InheritConstructors

@CompileStatic
interface InterpolableEnum<E extends Enum> extends InterpolableValue<Object, E, InterpolableEnum<E>> {
  abstract class ImmutableRaw<E extends Enum> extends InterpolableValue.ImmutableRaw<Object, E, InterpolableEnum, Interpolated, AlreadyInterpolated> implements InterpolableEnum<E> {
    private static final Utils<E> UTILS = new Utils<E>()

    ImmutableRaw() {
      super()
    }

    ImmutableRaw(E raw) {
      super(raw)
    }

    @JsonCreator
    ImmutableRaw(String raw) {
      super(UTILS.tryCastStringToEnum(raw))
    }

    protected final E doInterpolatePrimitive(Context context, E raw) {
      raw
    }

    protected final E doInterpolatePrimitive(Context context, SimpleInterpolableString raw) {
      UTILS.doInterpolatePrimitive context, raw
    }
  }

  abstract class Raw<E extends Enum> extends InterpolableValue.Raw<Object, E, InterpolableEnum, Interpolated, AlreadyInterpolated> implements InterpolableEnum<E> {
    private static final Utils<E> UTILS = new Utils<E>()

    Raw() {
      super()
    }

    Raw(E raw) {
      super(raw)
    }

    @JsonCreator
    Raw(String raw) {
      super(UTILS.tryCastStringToEnum(raw))
    }

    protected final E doInterpolatePrimitive(Context context, E raw) {
      raw
    }

    protected final E doInterpolatePrimitive(Context context, SimpleInterpolableString raw) {
      UTILS.doInterpolatePrimitive context, raw
    }
  }

  @InheritConstructors
  abstract class Interpolated<E extends Enum> extends InterpolableValue.Interpolated<Object, E, InterpolableEnum, AlreadyInterpolated> implements InterpolableEnum<E> { }

  @InheritConstructors
  abstract class AlreadyInterpolated<E extends Enum> extends InterpolableValue.AlreadyInterpolated<Object, E, InterpolableEnum> implements InterpolableEnum<E> { }

  private final static class Utils<E extends Enum> {
    @SuppressWarnings('UnstableApiUsage')
    private static final Class<E> ENUM_CLASS = (Class<E>)new TypeToken<E>(this.class) { }.rawType

    E doInterpolatePrimitive(Context context, SimpleInterpolableString raw) {
      Enum.valueOf ENUM_CLASS, raw.interpolate(context).toUpperCase()
    }

    Object tryCastStringToEnum(String raw) {
      String rawValueUpperCase = raw.toUpperCase()
      for (E enumConstant : ENUM_CLASS.enumConstants) {
        if (enumConstant.name() == rawValueUpperCase) {
          return enumConstant
        }
      }
      return new SimpleInterpolableString(raw)
    }
  }
}
