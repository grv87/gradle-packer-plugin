package org.fidata.packer.engine.ast.valid.customRegister.expected

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
abstract class CustomRegisterTest implements InterpolableObject<CustomRegisterTest> {
  private final InterpolableLong singleField

  @Input
  final InterpolableLong getSingleField() {
    this.@singleField
  }

  @Generated(value = 'org.fidata.packer.engine.ast.AutoImplementAstTransformation', date = '2018-12-29T07:52:26+03:00')
  protected CustomRegisterTest(
    InterpolableLong singleField
  ) {
    this.@singleField = singleField
  }

  @KnownImmutable
  @Generated(value = 'org.fidata.packer.engine.ast.AutoImplementAstTransformation', date = '2018-12-29T07:52:28+03:00')
  static final class ImmutableImpl extends CustomRegisterTest {
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
        singleField ?: engine.abstractTypeMappingRegistry.instantiate(InterpolableLong, Mutability.IMMUTABLE),
      )
    }
  }

  @Generated(value = 'org.fidata.packer.engine.ast.AutoImplementAstTransformation', date = '2018-12-29T07:52:33+03:00')
  static final class Impl extends CustomRegisterTest {
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
        singleField ?: engine.abstractTypeMappingRegistry.instantiate(InterpolableLong, Mutability.MUTABLE),
      )
    }
  }

  @Generated(value = 'org.fidata.packer.engine.ast.AutoImplementAstTransformation', date = '2018-12-29T07:52:37+03:00')
  static final class Interpolated extends CustomRegisterTest {
    protected Interpolated(Context context, CustomRegisterTest from) {
      super(
        from.@singleField.interpolateValue(context, 1L),
      )
    }
  }

  static final void register(AbstractEngine e) {
    // do some stuff
    new Random().nextInt()
    e.abstractTypeMappingRegistry.registerAbstractTypeMapping CustomRegisterTest, Impl, ImmutableImpl
  }

  @Override
  @Generated(value = 'org.fidata.packer.engine.ast.AutoImplementAstTransformation', date = '2018-12-29T07:52:41+03:00')
  final CustomRegisterTest interpolate(Context context) {
    return new Interpolated(context, this)
  }
}
