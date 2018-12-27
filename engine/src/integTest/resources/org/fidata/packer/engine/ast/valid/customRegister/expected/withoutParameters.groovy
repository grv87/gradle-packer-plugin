package org.fidata.packer.engine.ast.valid.customRegister.expected

import com.fasterxml.jackson.annotation.JacksonInject
import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.annotation.OptBoolean
import org.fidata.packer.engine.types.InterpolableLong
import org.fidata.packer.engine.types.base.InterpolableObject
import org.fidata.packer.engine.Mutability
import org.fidata.packer.engine.Engine
import com.github.hashicorp.packer.template.Context
import groovy.transform.CompileStatic
import groovy.transform.KnownImmutable
import org.gradle.api.tasks.Input

@CompileStatic
abstract class CustomRegisterTest implements InterpolableObject<CustomRegisterTest> {
  private final InterpolableLong singleField

  @Input
  final InterpolableLong getSingleField() {
    this.@singleField
  }

  protected CustomRegisterTest(
    InterpolableLong singleField
  ) {
    this.@singleField = singleField
  }

  @KnownImmutable
  static final class ImmutableImpl extends CustomRegisterTest {
    ImmutableImpl(Engine engine) {
      this(
        engine,
        (InterpolableLong)null,
      )
    }

    @JsonCreator
    ImmutableImpl(
      @JacksonInject(useInput = OptBoolean.FALSE)
        Engine engine,
      @JsonProperty('single_field')
        InterpolableLong singleField
    ) {
      super(
        singleField ?: engine.abstractTypeMappingRegistry.instantiate(InterpolableLong, Mutability.IMMUTABLE),
      )
    }
  }

  static final class Impl extends CustomRegisterTest {
    Impl(Engine engine) {
      this(
        engine,
        (InterpolableLong)null,
      )
    }

    @JsonCreator
    Impl(
      @JacksonInject(useInput = OptBoolean.FALSE)
      Engine engine,
      @JsonProperty('single_field')
      InterpolableLong singleField
    ) {
      super(
        singleField ?: engine.abstractTypeMappingRegistry.instantiate(InterpolableLong, Mutability.MUTABLE),
      )
    }
  }

  static final class Interpolated extends CustomRegisterTest {
    protected Interpolated(Context context, CustomRegisterTest from) {
      super(
        from.@singleField.interpolateValue(context, 1L),
      )
    }
  }

  static final void register(Engine e) {
    // do some stuff
    new Random().nextInt()
    e.abstractTypeMappingRegistry.registerAbstractTypeMapping CustomRegisterTest, Impl, ImmutableImpl
  }

  @Override
  final CustomRegisterTest interpolate(Context context) {
    return new Interpolated(context, this)
  }
}
