package org.fidata.packer.engine.types

import org.fidata.packer.engine.types.base.InterpolableValue
import org.fidata.packer.engine.types.base.SimpleInterpolableString
import com.github.hashicorp.packer.template.Context
import com.google.common.collect.ImmutableList
import groovy.transform.CompileStatic
import com.fasterxml.jackson.annotation.JsonCreator
import groovy.transform.InheritConstructors
import groovy.transform.KnownImmutable

@CompileStatic
interface InterpolableStringArray extends InterpolableValue<Object, ImmutableList<String>, InterpolableStringArray> {
  static final class ArrayClass extends ArrayList<SimpleInterpolableString> { }

  @KnownImmutable
  final class ImmutableRaw extends InterpolableValue.ImmutableRaw<Object, ImmutableList<String>, InterpolableStringArray, Interpolated, AlreadyInterpolated> implements InterpolableStringArray {
    ImmutableRaw() {
      super()
    }

    @JsonCreator(mode = JsonCreator.Mode.DELEGATING)
    ImmutableRaw(ArrayClass raw) {
      super(ImmutableList.copyOf(raw))
    }

    @JsonCreator(mode = JsonCreator.Mode.DELEGATING)
    /*
     * WORKAROUND:
     * Can't use argument of type SimpleInterpolableString
     * since Jackson doesn't work correctly with nested value classes
     * <grv87 2018-12-20>
     */
    ImmutableRaw(String raw) {
      super(new SimpleInterpolableString(raw))
    }

    protected static final ImmutableList<String> doInterpolatePrimitive(Context context, List<SimpleInterpolableString> raw) {
      ImmutableList.copyOf(raw*.interpolate(context))
    }

    protected static final ImmutableList<String> doInterpolatePrimitive(Context context, SimpleInterpolableString raw) {
      ImmutableList.copyOf(raw.interpolate(context).split(','))
    }
  }

  final class Raw extends InterpolableValue.Raw<Object, ImmutableList<String>, InterpolableStringArray, Interpolated, AlreadyInterpolated> implements InterpolableStringArray {
    Raw() {
      super()
    }

    @JsonCreator(mode = JsonCreator.Mode.DELEGATING)
    Raw(ArrayClass raw) {
      super(new ArrayList(raw))
    }

    @JsonCreator(mode = JsonCreator.Mode.DELEGATING)
    /*
     * WORKAROUND:
     * Can't use argument of type SimpleInterpolableString
     * since Jackson doesn't work correctly with nested value classes
     * <grv87 2018-12-20>
     */
    Raw(String raw) {
      super(new SimpleInterpolableString(raw))
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

  @KnownImmutable
  @InheritConstructors
  final class AlreadyInterpolated extends InterpolableValue.AlreadyInterpolated<Object, ImmutableList<String>, InterpolableStringArray> implements InterpolableStringArray { }
}
