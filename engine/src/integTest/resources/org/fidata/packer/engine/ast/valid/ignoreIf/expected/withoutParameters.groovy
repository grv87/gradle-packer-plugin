package org.fidata.packer.engine.ast.valid.ignoreIf.expected

import com.fasterxml.jackson.annotation.JacksonInject
import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.annotation.OptBoolean
import org.fidata.packer.engine.types.InterpolableLong
import org.fidata.packer.engine.types.base.InterpolableObject
import org.fidata.packer.engine.Mutability
import org.fidata.packer.engine.AbstractEngine
import com.github.hashicorp.packer.template.Context
import groovy.transform.CompileStatic
import groovy.transform.KnownImmutable
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.Internal
import org.gradle.api.tasks.Optional

import javax.annotation.Generated

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

  @Generated(value = 'org.fidata.packer.engine.ast.AutoImplementAstTransformation', date = '2018-12-29T07:50:39+03:00')
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
  @Generated(value = 'org.fidata.packer.engine.ast.AutoImplementAstTransformation', date = '2018-12-29T07:50:48+03:00')
  static final class ImmutableImpl extends IgnoreIfTest {
    ImmutableImpl(AbstractEngine engine) {
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
        AbstractEngine engine,
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

  @Generated(value = 'org.fidata.packer.engine.ast.AutoImplementAstTransformation', date = '2018-12-29T07:50:52+03:00')
  static final class Impl extends IgnoreIfTest {
    Impl(AbstractEngine engine) {
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
      AbstractEngine engine,
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

  @Generated(value = 'org.fidata.packer.engine.ast.AutoImplementAstTransformation', date = '2018-12-29T07:50:57+03:00')
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
  @Generated(value = 'org.fidata.packer.engine.ast.AutoImplementAstTransformation', date = '2018-12-29T07:51:01+03:00')
  final IgnoreIfTest interpolate(Context context) {
    return new Interpolated(context, this)
  }

  @Generated(value = 'org.fidata.packer.engine.ast.AutoImplementAstTransformation', date = '2018-12-29T07:51:04+03:00')
  static final void register(AbstractEngine engine) {
    engine.abstractTypeMappingRegistry.registerAbstractTypeMapping IgnoreIfTest, Impl, ImmutableImpl
  }
}
