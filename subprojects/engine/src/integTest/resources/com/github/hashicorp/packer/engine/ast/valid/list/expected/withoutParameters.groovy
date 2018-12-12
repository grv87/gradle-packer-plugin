import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty
import com.github.hashicorp.packer.engine.types.InterpolableInteger
import com.github.hashicorp.packer.engine.types.InterpolableObject
import com.github.hashicorp.packer.engine.utils.ObjectMapperFacade
import com.github.hashicorp.packer.template.Context
import com.google.common.collect.ImmutableList
import groovy.transform.CompileStatic
import org.gradle.api.tasks.Input

@CompileStatic
class ListTest implements InterpolableObject<ListTest> {
  private final List<InterpolableInteger> singleList

  @Input
  final List<InterpolableInteger> getSingleList() {
    this.@singleList
  }

  private ListTest(
    List<InterpolableInteger> singleList
  ) {
    this.@singleList = singleList
  }

  private ListTest(Context context, ListTest from) {
    this(
      ImmutableList.copyOf((List<InterpolableInteger>)from.@singleList.collect { InterpolableInteger interpolableItem -> interpolableItem.interpolateValue(context) }),
    )
  }

  static final class Impl extends ListTest {
    ListTestImpl() {
      this(
        (List<InterpolableInteger>)null,
      )
    }

    @JsonCreator
    ListTestImpl(
      @JsonProperty('single_field')
      List<InterpolableInteger> singleList
    ) {
      super(
        singleList != null ? (List<InterpolableInteger>)singleList.clone() : new ArrayList<InterpolableInteger>(),
      )
    }
  }

  static final class ImmutableImpl extends ListTest {
    ListTestImmutableImpl() {
      this(
        (List<InterpolableInteger>)null,
      )
    }

    @JsonCreator
    ListTestImmutableImpl(
      @JsonProperty('single_field')
      List<InterpolableInteger> singleList
    ) {
      super(
        singleList != null ? ImmutableList.copyOf(singleList) : ImmutableList.<InterpolableInteger>of(),
      )
    }
  }

  @Override
  final ListTest interpolate(Context context) {
    return new ListTest(context, this)
  }

  static {
    ObjectMapperFacade.ABSTRACT_TYPE_MAPPING_REGISTRY.registerAbstractTypeMapping ListTest, ListTestImpl, ListTestImmutableImpl
  }
}
