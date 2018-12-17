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

@CompileStatic
abstract class MinimalTest implements InterpolableObject<MinimalTest> {
  private final InterpolableLong singleField

  @Input
  final InterpolableLong getSingleField() {
    this.@singleField
  }

  private MinimalTest(
    InterpolableLong singleField
  ) {
    this.@singleField = singleField
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
      InterpolableLong singleField
    ) {
      super(
        singleField ?: engine.abstractTypeMappingRegistry.newInstance(InterpolableLong, Mutability.MUTABLE),
      )
    }
  }

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
      InterpolableLong singleField
    ) {
      super(
        singleField ?: engine.abstractTypeMappingRegistry.newInstance(InterpolableLong, Mutability.IMMUTABLE),
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
