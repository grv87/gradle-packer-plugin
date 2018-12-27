package org.fidata.packer.engine.ast.valid.ignoreIf.expected

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
import org.gradle.api.tasks.Internal
import org.gradle.api.tasks.Optional

@CompileStatic
abstract class IgnoreIfTest implements InterpolableObject<IgnoreIfTest> {
  private final InterpolableLong firstField
  private final InterpolableLong secondField
  private final InterpolableLong thirdField

  @Input
  final InterpolableLong getFirstField() {
    this.@firstField
  }

  @Input
  @Optional
  final InterpolableLong getSecondField() {
    this.@secondField
  }

  @Internal
  final InterpolableLong getThirdField() {
    this.@thirdField
  }

  protected IgnoreIfTest(
    InterpolableLong firstField,
    InterpolableLong secondField,
    InterpolableLong thirdField
  ) {
    this.@firstField = firstField
    this.@secondField = secondField
    this.@thirdField = thirdField
  }

  @KnownImmutable
  static final class ImmutableImpl extends IgnoreIfTest {
    ImmutableImpl(Engine engine) {
      this(
        engine,
        (InterpolableLong)null,
        (InterpolableLong)null,
        (InterpolableLong)null,
      )
    }

    @JsonCreator
    ImmutableImpl(
      @JacksonInject(useInput = OptBoolean.FALSE)
        Engine engine,
      @JsonProperty('first_field')
        InterpolableLong firstField,
      @JsonProperty('second_field')
        InterpolableLong secondField,
      @JsonProperty('third_field')
        InterpolableLong thirdField
    ) {
      super(
        firstField ?: engine.abstractTypeMappingRegistry.instantiate(InterpolableLong, Mutability.IMMUTABLE),
        secondField ?: engine.abstractTypeMappingRegistry.instantiate(InterpolableLong, Mutability.IMMUTABLE),
        thirdField ?: engine.abstractTypeMappingRegistry.instantiate(InterpolableLong, Mutability.IMMUTABLE),
      )
    }
  }

  static final class Impl extends IgnoreIfTest {
    Impl(Engine engine) {
      this(
        engine,
        (InterpolableLong)null,
        (InterpolableLong)null,
        (InterpolableLong)null,
      )
    }

    @JsonCreator
    Impl(
      @JacksonInject(useInput = OptBoolean.FALSE)
      Engine engine,
      @JsonProperty('first_field')
      InterpolableLong firstField,
      @JsonProperty('second_field')
      InterpolableLong secondField,
      @JsonProperty('third_field')
      InterpolableLong thirdField
    ) {
      super(
        firstField ?: engine.abstractTypeMappingRegistry.instantiate(InterpolableLong, Mutability.MUTABLE),
        secondField ?: engine.abstractTypeMappingRegistry.instantiate(InterpolableLong, Mutability.MUTABLE),
        thirdField ?: engine.abstractTypeMappingRegistry.instantiate(InterpolableLong, Mutability.MUTABLE),
      )
    }
  }

  static final class Interpolated extends IgnoreIfTest {
    protected Interpolated(Context context, IgnoreIfTest from) {
      super(
        from.@firstField.interpolateValue(context),
        from.@secondField.interpolateValue(context, (Long)null, { -> firstField.interpolated != 0 }),
        from.@thirdField.interpolateValue(context, (Long)null, { -> firstField.interpolated == 42 }),
      )
    }
  }

  @Override
  final IgnoreIfTest interpolate(Context context) {
    return new Interpolated(context, this)
  }

  static final void register(Engine engine) {
    engine.abstractTypeMappingRegistry.registerAbstractTypeMapping IgnoreIfTest, Impl, ImmutableImpl
  }
}
