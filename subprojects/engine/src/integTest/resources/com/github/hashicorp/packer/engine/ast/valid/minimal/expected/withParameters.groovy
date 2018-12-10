import com.fasterxml.jackson.annotation.JsonCreator
import com.github.hashicorp.packer.engine.types.InterpolableLong
import com.github.hashicorp.packer.engine.types.InterpolableObject
import com.github.hashicorp.packer.engine.utils.Mutability
import com.github.hashicorp.packer.engine.utils.ObjectMapperFacade
import com.github.hashicorp.packer.template.Context
import groovy.transform.CompileStatic
import org.gradle.api.tasks.Input

@CompileStatic
class MinimalTest implements InterpolableObject<MinimalTest> {
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

  private MinimalTest(Context context, MinimalTest from) {
    this(
      from.@singleField.interpolateValue(context, 1L),
    )
  }

  static final class MinimalTestImpl extends MinimalTest {
    MinimalTestImpl() {
      this(
        (InterpolableLong)null,
      )
    }

    @JsonCreator
    MinimalTestImpl(
      InterpolableLong singleField
    ) {
      super(
        singleField ?: ObjectMapperFacade.ABSTRACT_TYPE_MAPPING_REGISTRY.newInstance(InterpolableLong, Mutability.MUTABLE),
      )
    }
  }

  static final class MinimalTestImmutableImpl extends MinimalTest {
    MinimalTestImmutableImpl() {
      this(
        (InterpolableLong)null,
      )
    }

    @JsonCreator
    MinimalTestImmutableImpl(
      InterpolableLong singleField
    ) {
      super(
        singleField ?: ObjectMapperFacade.ABSTRACT_TYPE_MAPPING_REGISTRY.newInstance(InterpolableLong, Mutability.IMMUTABLE),
      )
    }
  }

  @Override
  final MinimalTest interpolate(Context context) {
    return new MinimalTest(context, this)
  }

  static {
    ObjectMapperFacade.ABSTRACT_TYPE_MAPPING_REGISTRY.registerAbstractTypeMapping MinimalTest, MinimalTestImpl, MinimalTestImmutableImpl
  }
}
