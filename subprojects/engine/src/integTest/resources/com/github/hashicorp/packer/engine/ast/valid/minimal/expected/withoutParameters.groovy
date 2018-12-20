import com.fasterxml.jackson.annotation.JacksonInject
import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.annotation.OptBoolean
import com.github.hashicorp.packer.engine.types.InterpolableLong
import com.github.hashicorp.packer.engine.types.base.InterpolableObject
import com.github.hashicorp.packer.engine.Mutability
import com.github.hashicorp.packer.engine.Engine
import com.github.hashicorp.packer.template.Context
import groovy.transform.CompileStatic
import groovy.transform.KnownImmutable
import org.gradle.api.tasks.Input

@CompileStatic
abstract class MinimalTest implements InterpolableObject<MinimalTest> {
  private final InterpolableLong singleField

  @Input
  final InterpolableLong getSingleField() {
    this.@singleField
  }

  protected MinimalTest(
    InterpolableLong singleField
  ) {
    this.@singleField = singleField
  }

  @KnownImmutable
  static final class ImmutableImpl extends MinimalTest {
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

  static final class Impl extends MinimalTest {
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

  static final class Interpolated extends MinimalTest {
    protected Interpolated(Context context, MinimalTest from) {
      super(
        from.@singleField.interpolateValue(context, 1L),
      )
    }
  }

  @Override
  final MinimalTest interpolate(Context context) {
    return new Interpolated(context, this)
  }

  static final void register(Engine engine) {
    engine.abstractTypeMappingRegistry.registerAbstractTypeMapping MinimalTest, Impl, ImmutableImpl
  }
}
