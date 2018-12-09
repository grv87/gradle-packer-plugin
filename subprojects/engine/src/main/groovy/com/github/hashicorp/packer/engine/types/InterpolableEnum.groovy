package com.github.hashicorp.packer.engine.types

import com.fasterxml.jackson.annotation.JsonCreator
import com.github.hashicorp.packer.template.Context
import com.google.common.reflect.TypeToken
import groovy.transform.CompileStatic
import groovy.transform.InheritConstructors

@CompileStatic
interface InterpolableEnum<
  E extends Enum,
  ThisInterface extends InterpolableEnum<E, ThisInterface>
> extends InterpolableValue<Object, E, ThisInterface> {
  abstract class ImmutableRaw<
    E extends Enum,
    ThisInterface extends InterpolableEnum<E, ThisInterface>,
    InterpolatedClass extends Interpolated<E, ThisInterface, AlreadyInterpolatedClass> & ThisInterface,
    AlreadyInterpolatedClass extends AlreadyInterpolated<E, ThisInterface> & ThisInterface
  > extends InterpolableValue.ImmutableRaw<Object, E, InterpolableEnum, InterpolatedClass, AlreadyInterpolatedClass> implements InterpolableEnum<E, ThisInterface> {
    @SuppressWarnings('UnstableApiUsage')
    private final Class<E> enumClass = (Class<E>)new TypeToken<E>(this.class) { }.rawType

    ImmutableRaw() {
      super()
    }

    ImmutableRaw(E raw) {
      super(raw)
    }

    @JsonCreator
    ImmutableRaw(String raw) {
      super(tryCastStringToEnum((Class<E>)new TypeToken<E>(this.class) { }.rawType, raw))
    }

    protected final E doInterpolatePrimitive(Context context, E raw) {
      raw
    }

    protected final E doInterpolatePrimitive(Context context, SimpleInterpolableString raw) {
      Enum.valueOf enumClass, raw.interpolate(context).toUpperCase()
    }

    private static Object tryCastStringToEnum(Class<? extends Enum> enumClass, String raw) {
      String rawValueUpperCase = raw.toUpperCase()
      for (/*E*/Enum enumConstant : enumClass.enumConstants) {
        if (enumConstant.name() == rawValueUpperCase) {
          return enumConstant
        }
      }
      return new SimpleInterpolableString(raw)
    }
  }

  abstract class Raw<
    E extends Enum,
    ThisInterface extends InterpolableEnum<E, ThisInterface>,
    InterpolatedClass extends Interpolated<E, ThisInterface, AlreadyInterpolatedClass> & ThisInterface,
    AlreadyInterpolatedClass extends AlreadyInterpolated<E, ThisInterface> & ThisInterface
  > extends InterpolableValue.Raw<Object, E, InterpolableEnum, InterpolatedClass, AlreadyInterpolatedClass> implements InterpolableEnum<E, ThisInterface> {
    @SuppressWarnings('UnstableApiUsage')
    private final Class<E> enumClass = (Class<E>)new TypeToken<E>(this.class) { }.rawType

    Raw() {
      super()
    }

    Raw(E raw) {
      super(raw)
    }

    @JsonCreator
    Raw(String raw) {
      super(tryCastStringToEnum((Class<E>)new TypeToken<E>(this.class) { }.rawType, raw))
    }

    protected final E doInterpolatePrimitive(Context context, E raw) {
      raw
    }

    protected final E doInterpolatePrimitive(Context context, SimpleInterpolableString raw) {
      Enum.valueOf enumClass, raw.interpolate(context).toUpperCase()
    }

    private static Object tryCastStringToEnum(Class<? extends Enum> enumClass, String raw) {
      String rawValueUpperCase = raw.toUpperCase()
      for (/*E*/Enum enumConstant : enumClass.enumConstants) {
        if (enumConstant.name() == rawValueUpperCase) {
          return enumConstant
        }
      }
      return new SimpleInterpolableString(raw)
    }
  }

  @InheritConstructors
  abstract class Interpolated<
    E extends Enum,
    ThisInterface extends InterpolableEnum<E, ThisInterface>,
    AlreadyInterpolatedClass extends AlreadyInterpolated<E, ThisInterface> & ThisInterface
  > extends InterpolableValue.Interpolated<Object, E, InterpolableEnum, AlreadyInterpolatedClass> implements InterpolableEnum<E, ThisInterface> { }

  @InheritConstructors
  abstract class AlreadyInterpolated<
    E extends Enum,
    ThisInterface extends InterpolableEnum<E, ThisInterface>
  > extends InterpolableValue.AlreadyInterpolated<Object, E, InterpolableEnum> implements InterpolableEnum<E, ThisInterface> { }
}
