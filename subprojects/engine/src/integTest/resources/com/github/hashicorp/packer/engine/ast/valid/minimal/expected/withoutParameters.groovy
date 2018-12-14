import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty
import com.github.hashicorp.packer.engine.types.InterpolableLong
import com.github.hashicorp.packer.engine.types.InterpolableObject
import com.github.hashicorp.packer.engine.utils.Mutability
import com.github.hashicorp.packer.engine.utils.ObjectMapperFacade
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
    Impl() {
      this(
        (InterpolableLong)null,
      )
    }

    @JsonCreator
    Impl(
      @JsonProperty('single_field')
      InterpolableLong singleField
    ) {
      super(
        singleField ?: ObjectMapperFacade.ABSTRACT_TYPE_MAPPING_REGISTRY.newInstance(InterpolableLong, Mutability.MUTABLE),
      )
    }
  }

  static final class ImmutableImpl extends MinimalTest {
    ImmutableImpl() {
      this(
        (InterpolableLong)null,
      )
    }

    @JsonCreator
    ImmutableImpl(
      @JsonProperty('single_field')
      InterpolableLong singleField
    ) {
      super(
        singleField ?: ObjectMapperFacade.ABSTRACT_TYPE_MAPPING_REGISTRY.newInstance(InterpolableLong, Mutability.IMMUTABLE),
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

  static final void register() {
    ObjectMapperFacade.ABSTRACT_TYPE_MAPPING_REGISTRY.registerAbstractTypeMapping MinimalTest, Impl, ImmutableImpl
  }
}
