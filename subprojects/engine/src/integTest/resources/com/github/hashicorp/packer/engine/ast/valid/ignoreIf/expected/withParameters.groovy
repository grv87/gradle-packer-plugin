import com.fasterxml.jackson.annotation.JacksonInject
import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.OptBoolean
import com.github.hashicorp.packer.engine.types.InterpolableLong
import com.github.hashicorp.packer.engine.types.InterpolableObject
import com.github.hashicorp.packer.engine.Mutability
import com.github.hashicorp.packer.engine.Engine
import com.github.hashicorp.packer.template.Context
import groovy.transform.CompileStatic
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

  private IgnoreIfTest(
    InterpolableLong firstField,
    InterpolableLong secondField,
    InterpolableLong thirdField
  ) {
    this.@firstField = firstField
    this.@secondField = secondField
    this.@thirdField = thirdField
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
      InterpolableLong firstField,
      InterpolableLong secondField,
      InterpolableLong thirdField
    ) {
      super(
        firstField ?: engine.abstractTypeMappingRegistry.newInstance(InterpolableLong, Mutability.MUTABLE),
        secondField ?: engine.abstractTypeMappingRegistry.newInstance(InterpolableLong, Mutability.MUTABLE),
        thirdField ?: engine.abstractTypeMappingRegistry.newInstance(InterpolableLong, Mutability.MUTABLE),
      )
    }
  }

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
      InterpolableLong firstField,
      InterpolableLong secondField,
      InterpolableLong thirdField
    ) {
      super(
        firstField ?: engine.abstractTypeMappingRegistry.newInstance(InterpolableLong, Mutability.IMMUTABLE),
        secondField ?: engine.abstractTypeMappingRegistry.newInstance(InterpolableLong, Mutability.IMMUTABLE),
        thirdField ?: engine.abstractTypeMappingRegistry.newInstance(InterpolableLong, Mutability.IMMUTABLE),
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
