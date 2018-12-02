package com.github.hashicorp.packer.engine.types

import com.github.hashicorp.packer.template.Context
import com.google.common.collect.ImmutableList
import groovy.transform.CompileStatic
import com.fasterxml.jackson.annotation.JsonCreator
import groovy.transform.InheritConstructors

@CompileStatic
interface InterpolableStringArray extends InterpolableValue<Object, ImmutableList<String>, InterpolableStringArray> {
  final class ImmutableRaw extends InterpolableValue.ImmutableRaw<Object, ImmutableList<String>, InterpolableStringArray, Interpolated, AlreadyInterpolated> implements InterpolableStringArray {
    ImmutableRaw() {
      super()
    }

    @JsonCreator
    ImmutableRaw(List<String> raw) {
      super(ImmutableList.copyOf(raw))
    }

    @JsonCreator
    ImmutableRaw(SimpleInterpolableString raw) {
      super(raw)
    }

    protected static final ImmutableList<String> doInterpolatePrimitive(Context context, List<SimpleInterpolableString> raw) {
      ImmutableList.copyOf(raw*.interpolate(context))
    }

    protected static final ImmutableList<String> doInterpolatePrimitive(Context context, SimpleInterpolableString raw) {
      ImmutableList.copyOf(raw.interpolate(context).split(','))
    }
  }

  final class Raw extends InterpolableValue.Raw<Object, ImmutableList<String>, InterpolableStringArray, Interpolated, AlreadyInterpolated> implements InterpolableStringArray {
    static final class ArrayClass extends ArrayList<SimpleInterpolableString> { }

    Raw() {
      super()
    }

    @JsonCreator
    Raw(List<String> raw) {
      super(ImmutableList.copyOf(raw))
    }

    @JsonCreator
    Raw(SimpleInterpolableString raw) {
      super(raw)
    }

    protected static final ImmutableList<String> doInterpolatePrimitive(Context context, List<SimpleInterpolableString> raw) {
      ImmutableList.copyOf(raw*.interpolate(context))
    }

    protected static final ImmutableList<String> doInterpolatePrimitive(Context context, SimpleInterpolableString raw) {
      ImmutableList.copyOf(raw.interpolate(context).split(','))
    }
  }

  @InheritConstructors
  final class Interpolated extends InterpolableValue.Interpolated<Object, ImmutableList<String>, InterpolableStringArray, AlreadyInterpolated> implements InterpolableStringArray { }

  @InheritConstructors
  final class AlreadyInterpolated extends InterpolableValue.AlreadyInterpolated<Object, ImmutableList<String>, InterpolableStringArray> implements InterpolableStringArray { }
}
