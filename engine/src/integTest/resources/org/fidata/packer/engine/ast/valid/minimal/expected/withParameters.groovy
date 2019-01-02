package org.fidata.packer.engine.ast.valid.minimal.expected

import com.fasterxml.jackson.annotation.JacksonInject
import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.OptBoolean
import org.fidata.packer.engine.types.InterpolableLong
import org.fidata.packer.engine.types.base.InterpolableObject
import org.fidata.packer.engine.Mutability
import org.fidata.packer.engine.AbstractEngine
import com.github.hashicorp.packer.template.Context
import groovy.transform.CompileStatic
import groovy.transform.KnownImmutable
import org.gradle.api.tasks.Input

import javax.annotation.Generated

@CompileStatic
abstract class MinimalTest implements InterpolableObject<MinimalTest> {
  private final InterpolableLong singleField

  @Input
  final InterpolableLong getSingleField() {
    this.@singleField
  }

  @Generated(value = 'org.fidata.packer.engine.ast.AutoImplementAstTransformation', date = '2018-12-29T07:47:03+03:00')
  protected MinimalTest(
    InterpolableLong singleField
  ) {
    this.@singleField = singleField
  }

  @KnownImmutable
  @Generated(value = 'org.fidata.packer.engine.ast.AutoImplementAstTransformation', date = '2018-12-29T07:46:05+03:00')
  static final class ImmutableImpl extends MinimalTest {
    ImmutableImpl(AbstractEngine engine) {
      this(
        engine,
        (InterpolableLong)null,
      )
    }

    @JsonCreator
    ImmutableImpl(
      @JacksonInject(useInput = OptBoolean.FALSE)
        AbstractEngine engine,
      InterpolableLong singleField
    ) {
      super(
        singleField ?: engine.instantiate(InterpolableLong, Mutability.IMMUTABLE),
      )
    }
  }

  @Generated(value = 'org.fidata.packer.engine.ast.AutoImplementAstTransformation', date = '2018-12-29T07:44:25+03:00')
  static final class Impl extends MinimalTest {
    Impl(AbstractEngine engine) {
      this(
        engine,
        (InterpolableLong)null,
      )
    }

    @JsonCreator
    Impl(
      @JacksonInject(useInput = OptBoolean.FALSE)
      AbstractEngine engine,
      InterpolableLong singleField
    ) {
      super(
        singleField ?: engine.instantiate(InterpolableLong, Mutability.MUTABLE),
      )
    }
  }

  @Generated(value = 'org.fidata.packer.engine.ast.AutoImplementAstTransformation', date = '2018-12-29T07:47:13+03:00')
  static final class Interpolated extends MinimalTest {
    protected Interpolated(Context context, MinimalTest from) {
      super(
        from.@singleField.interpolateValue(context, 1L),
      )
    }
  }

  @Override
  @Generated(value = 'org.fidata.packer.engine.ast.AutoImplementAstTransformation', date = '2018-12-29T07:47:22+03:00')
  final MinimalTest interpolate(Context context) {
    return new Interpolated(context, this)
  }

  @Generated(value = 'org.fidata.packer.engine.ast.AutoImplementAstTransformation', date = '2018-12-29T07:47:29+03:00')
  static final void register(AbstractEngine engine) {
    engine.registerAbstractTypeMapping MinimalTest, Impl, ImmutableImpl
  }
}
