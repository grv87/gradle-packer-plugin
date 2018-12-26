package com.github.hashicorp.packer.engine.types

import com.fasterxml.jackson.annotation.JsonCreator
import com.github.hashicorp.packer.engine.annotations.ComputedInternal
import com.github.hashicorp.packer.engine.exceptions.ValueNotInterpolatedYetException
import com.github.hashicorp.packer.engine.types.base.InterpolableValue
import com.github.hashicorp.packer.engine.types.base.SimpleInterpolableString
import com.github.hashicorp.packer.template.Context
import groovy.transform.CompileStatic
import groovy.transform.InheritConstructors
import groovy.transform.KnownImmutable

@CompileStatic
interface InterpolableURI<ThisInterface extends InterpolableURI<ThisInterface>> extends InterpolableValue<Object, URI, ThisInterface> {
  @ComputedInternal
  URI getFileURI()

  @ComputedInternal
  URI getNonFileURI()

  @KnownImmutable
  class ImmutableRaw<
    ThisInterface extends InterpolableURI<ThisInterface>,
    InterpolatedClass extends Interpolated<ThisInterface, AlreadyInterpolatedClass>,
    AlreadyInterpolatedClass extends AlreadyInterpolated<ThisInterface>
  > extends InterpolableValue.ImmutableRaw<Object, URI, InterpolableURI, InterpolatedClass, AlreadyInterpolatedClass> implements InterpolableURI<ThisInterface> {
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

    @Override
    URI getFileURI() {
      throw new ValueNotInterpolatedYetException()
    }

    @Override
    URI getNonFileURI() {
      throw new ValueNotInterpolatedYetException()
    }
  }

  class Raw<
    ThisInterface extends InterpolableURI<ThisInterface>,
    InterpolatedClass extends Interpolated<ThisInterface, AlreadyInterpolatedClass>,
    AlreadyInterpolatedClass extends AlreadyInterpolated<ThisInterface>
    > extends InterpolableValue.Raw<Object, URI, InterpolableURI, InterpolatedClass, AlreadyInterpolatedClass> implements InterpolableURI<ThisInterface> {
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

    @Override
    URI getFileURI() {
      throw new ValueNotInterpolatedYetException()
    }

    @Override
    URI getNonFileURI() {
      throw new ValueNotInterpolatedYetException()
    }
  }

  @InheritConstructors
  class Interpolated<
    ThisInterface extends InterpolableURI<ThisInterface>,
    AlreadyInterpolatedClass extends AlreadyInterpolated<ThisInterface>
  > extends InterpolableValue.Interpolated<Object, URI, InterpolableURI, AlreadyInterpolatedClass> implements InterpolableURI<ThisInterface> {
    @Override
    URI getFileURI() {
      /*
       * WORKAROUND:
       * Without equals we got compilation error:
       * [Static type checking] - Cannot find matching method org.codehaus.groovy.runtime.ScriptBytecodeAdapter#compareEquals(java.lang.String, java.lang.String). Please check if the declared type is correct and if the method exists.
       * <grv87 2018-12-09>
       */
      'file'.equals(interpolated?.scheme) ? interpolated : null
    }

    @Override
    URI getNonFileURI() {
      /*
       * WORKAROUND:
       * Without equals we got compilation error:
       * [Static type checking] - Cannot find matching method org.codehaus.groovy.runtime.ScriptBytecodeAdapter#compareEquals(java.lang.String, java.lang.String). Please check if the declared type is correct and if the method exists.
       * <grv87 2018-12-09>
       */
      !'file'.equals(interpolated?.scheme) ? interpolated : null
    }
  }

  @KnownImmutable
  @InheritConstructors
  class AlreadyInterpolated<
    ThisInterface extends InterpolableURI<ThisInterface>
  > extends InterpolableValue.AlreadyInterpolated<Object, URI, InterpolableURI> implements InterpolableURI<ThisInterface> {
    @Override
    URI getFileURI() {
      /*
       * WORKAROUND:
       * Without equals we got compilation error:
       * [Static type checking] - Cannot find matching method org.codehaus.groovy.runtime.ScriptBytecodeAdapter#compareEquals(java.lang.String, java.lang.String). Please check if the declared type is correct and if the method exists.
       * <grv87 2018-12-09>
       */
      'file'.equals(interpolated?.scheme) ? interpolated : null
    }

    @Override
    URI getNonFileURI() {
      /*
       * WORKAROUND:
       * Without equals we got compilation error:
       * [Static type checking] - Cannot find matching method org.codehaus.groovy.runtime.ScriptBytecodeAdapter#compareEquals(java.lang.String, java.lang.String). Please check if the declared type is correct and if the method exists.
       * <grv87 2018-12-09>
       */
      !'file'.equals(interpolated?.scheme) ? interpolated : null
    }
  }
}
