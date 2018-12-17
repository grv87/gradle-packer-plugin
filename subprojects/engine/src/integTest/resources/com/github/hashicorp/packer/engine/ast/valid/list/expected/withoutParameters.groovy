import com.fasterxml.jackson.annotation.JacksonInject
import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.annotation.OptBoolean
import com.github.hashicorp.packer.engine.types.InterpolableInteger
import com.github.hashicorp.packer.engine.types.InterpolableObject
import com.github.hashicorp.packer.engine.Engine
import com.github.hashicorp.packer.template.Context
import com.google.common.collect.ImmutableList
import groovy.transform.CompileStatic
import org.gradle.api.tasks.Input

@CompileStatic
abstract class ListTest implements InterpolableObject<ListTest> {
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

  static final class Impl extends ListTest {
    Impl(Engine engine) {
      this(
        engine,
        (List<InterpolableInteger>)null,
      )
    }

    @JsonCreator
    Impl(
      @JacksonInject(useInput = OptBoolean.FALSE)
      Engine engine,
      @JsonProperty('single_field')
      List<InterpolableInteger> singleList
    ) {
      super(
        singleList != null ? (List<InterpolableInteger>)singleList.clone() : new ArrayList<InterpolableInteger>(),
      )
    }
  }

  static final class ImmutableImpl extends ListTest {
    ImmutableImpl(Engine engine) {
      this(
        engine,
        (List<InterpolableInteger>)null,
      )
    }

    @JsonCreator
    ImmutableImpl(
      @JacksonInject(useInput = OptBoolean.FALSE)
        Engine engine,
      @JsonProperty('single_field')
      List<InterpolableInteger> singleList
    ) {
      super(
        singleList != null ? ImmutableList.copyOf(singleList) : ImmutableList.<InterpolableInteger>of(),
      )
    }
  }

  static final class Interpolated extends ListTest {
    protected Interpolated(Context context, ListTest from) {
      super(
        ImmutableList.copyOf((List<InterpolableInteger>)from.@singleList.collect { InterpolableInteger interpolableItem -> interpolableItem.interpolateValue(context) }),
      )
    }
  }

  @Override
  final ListTest interpolate(Context context) {
    return new Interpolated(context, this)
  }

  static final void register(Engine engine) {
    engine.abstractTypeMappingRegistry.registerAbstractTypeMapping ListTest, Impl,ImmutableImpl
  }
}
