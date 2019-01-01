package org.fidata.packer.engine.types

import com.fasterxml.jackson.annotation.JsonCreator
import org.fidata.packer.engine.types.base.InterpolableValue
import org.fidata.packer.engine.types.base.SimpleInterpolableString
import com.github.hashicorp.packer.template.Context
import groovy.transform.CompileStatic
import groovy.transform.InheritConstructors
import groovy.transform.KnownImmutable

@CompileStatic
interface InterpolableURI extends InterpolableValue<Object, URI, InterpolableURI> {
  @KnownImmutable
  class ImmutableRaw extends InterpolableValue.ImmutableRaw<Object, URI, InterpolableURI, Interpolated, AlreadyInterpolated> implements InterpolableURI {
    ImmutableRaw() {
      super()
    }

    @JsonCreator(mode = JsonCreator.Mode.DELEGATING)
    ImmutableRaw(URI raw) {
      super(raw)
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

    protected static final URI doInterpolatePrimitive(Context context, URI raw) {
      raw
    }

    protected static final URI doInterpolatePrimitive(Context context, SimpleInterpolableString raw) {
      context.resolveUri raw.interpolate(context)
    }
  }

  class Raw extends InterpolableValue.Raw<Object, URI, InterpolableURI, Interpolated, AlreadyInterpolated> implements InterpolableURI {
    Raw() {
      super()
    }

    @JsonCreator(mode = JsonCreator.Mode.DELEGATING)
    Raw(URI raw) {
      super(raw)
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

    protected static final URI doInterpolatePrimitive(Context context, URI raw) {
      raw
    }

    protected static final URI doInterpolatePrimitive(Context context, SimpleInterpolableString raw) {
      context.resolveUri raw.interpolate(context)
    }
  }

  @InheritConstructors
  class Interpolated extends InterpolableValue.Interpolated<Object, URI, InterpolableURI, AlreadyInterpolated> implements InterpolableURI {}

  @KnownImmutable
  @InheritConstructors
  class AlreadyInterpolated extends InterpolableValue.AlreadyInterpolated<Object, URI, InterpolableURI> implements InterpolableURI {}
}
