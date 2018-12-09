import com.fasterxml.jackson.annotation.JsonProperty
import com.github.hashicorp.packer.engine.types.InterpolableLong
import com.github.hashicorp.packer.engine.types.InterpolableObject
import com.github.hashicorp.packer.engine.utils.Mutability
import com.github.hashicorp.packer.engine.utils.ObjectMapperFacade
import com.github.hashicorp.packer.template.Context
import groovy.transform.CompileStatic
import org.gradle.api.tasks.Input

@CompileStatic
abstract class Minimal implements InterpolableObject<Minimal> {
  private final InterpolableLong singleField

  private Minimal(
    InterpolableLong singleField
  ) {
    this.@singleField = singleField
  }

  @Input
  final InterpolableLong getSingleField() {
    this.@singleField
  }

  private static final class MinimalMutableImpl extends Minimal {
    MinimalMutableImpl() {
      this(null)
    }

    MinimalMutableImpl(
      @JsonProperty('single_field')
      InterpolableLong singleField
    ) {
      super(singleField ?: ObjectMapperFacade.ABSTRACT_TYPE_MAPPING_REGISTRY.newInstance(InterpolableLong, Mutability.MUTABLE))
    }
  }

  private static final class MinimalImmutableImpl extends Minimal {
    MinimalImmutableImpl() {
      this(null)
    }

    MinimalImmutableImpl(
      @JsonProperty('single_field')
      InterpolableLong singleField
    ) {
      super(
        singleField ?: ObjectMapperFacade.ABSTRACT_TYPE_MAPPING_REGISTRY.newInstance(InterpolableLong, Mutability.IMMUTABLE),
      )
    }

    private MinimalImmutableImpl(Context context, Minimal from) {
      super(
        from.@singleField.interpolateValue(context, 1L),
      )
    }
  }

  @Override
  final Minimal interpolate(Context context) {
    return new MinimalImmutableImpl(context, this)
  }
}