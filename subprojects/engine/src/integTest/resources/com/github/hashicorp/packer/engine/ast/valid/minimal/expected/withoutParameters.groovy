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
class Minimal implements InterpolableObject<Minimal> {
  private final InterpolableLong singleField

  @Input
  final InterpolableLong getSingleField() {
    this.@singleField
  }

  private Minimal(
    InterpolableLong singleField
  ) {
    this.@singleField = singleField
  }

  private Minimal(Context context, Minimal from) {
    this(
      from.@singleField.interpolateValue(context, 1L),
    )
  }

  static final class MinimalImpl extends Minimal {
    MinimalImpl() {
      this(
        (InterpolableLong)null,
      )
    }

    @JsonCreator
    MinimalImpl(
      @JsonProperty('single_field')
      InterpolableLong singleField
    ) {
      super(singleField ?: ObjectMapperFacade.ABSTRACT_TYPE_MAPPING_REGISTRY.newInstance(InterpolableLong, Mutability.MUTABLE))
    }
  }

  static final class MinimalImmutableImpl extends Minimal {
    MinimalImmutableImpl() {
      this(
        (InterpolableLong)null,
      )
    }

    @JsonCreator
    MinimalImmutableImpl(
      @JsonProperty('single_field')
      InterpolableLong singleField
    ) {
      super(
        singleField ?: ObjectMapperFacade.ABSTRACT_TYPE_MAPPING_REGISTRY.newInstance(InterpolableLong, Mutability.IMMUTABLE),
      )
    }
  }

  @Override
  final Minimal interpolate(Context context) {
    return new Minimal(context, this)
  }

  static {
    ObjectMapperFacade.ABSTRACT_TYPE_MAPPING_REGISTRY.registerAbstractTypeMapping Minimal, MinimalImpl, MinimalImmutableImpl
  }
}
