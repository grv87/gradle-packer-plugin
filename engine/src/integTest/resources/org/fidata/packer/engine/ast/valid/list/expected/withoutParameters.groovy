package org.fidata.packer.engine.ast.valid.list.expected

import com.fasterxml.jackson.annotation.JacksonInject
import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.annotation.OptBoolean
import org.fidata.packer.engine.types.InterpolableInteger
import org.fidata.packer.engine.types.base.InterpolableObject
import org.fidata.packer.engine.AbstractEngine
import com.github.hashicorp.packer.template.Context
import com.google.common.collect.ImmutableList
import groovy.transform.CompileStatic
import groovy.transform.KnownImmutable
import org.gradle.api.tasks.Input

import javax.annotation.Generated

@CompileStatic
abstract class ListTest implements InterpolableObject<ListTest> {
  private final List<InterpolableInteger> singleList

  @Input
  final List<InterpolableInteger> getSingleList() {
    this.@singleList
  }

  @Generated(value = 'org.fidata.packer.engine.ast.AutoImplementAstTransformation', date = '2018-12-29T07:49:15+03:00')
  protected ListTest(
    List<InterpolableInteger> singleList
  ) {
    this.@singleList = singleList
  }

  @KnownImmutable
  @Generated(value = 'org.fidata.packer.engine.ast.AutoImplementAstTransformation', date = '2018-12-29T07:49:20+03:00')
  static final class ImmutableImpl extends ListTest {
    ImmutableImpl(AbstractEngine engine) {
      this(
        engine,
        (List<InterpolableInteger>)null,
      )
    }

    @JsonCreator
    ImmutableImpl(
      @JacksonInject(useInput = OptBoolean.FALSE)
        AbstractEngine engine,
      @JsonProperty('single_list')
        List<InterpolableInteger> singleList
    ) {
      super(
        singleList != null ? ImmutableList.copyOf(singleList) : ImmutableList.<InterpolableInteger>of(),
      )
    }
  }

  @Generated(value = 'org.fidata.packer.engine.ast.AutoImplementAstTransformation', date = '2018-12-29T07:49:27+03:00')
  static final class Impl extends ListTest {
    Impl(AbstractEngine engine) {
      this(
        engine,
        (List<InterpolableInteger>)null,
      )
    }

    @JsonCreator
    Impl(
      @JacksonInject(useInput = OptBoolean.FALSE)
      AbstractEngine engine,
      @JsonProperty('single_list')
      List<InterpolableInteger> singleList
    ) {
      super(
        singleList != null ? new ArrayList(singleList) : new ArrayList<InterpolableInteger>(),
      )
    }
  }

  @Generated(value = 'org.fidata.packer.engine.ast.AutoImplementAstTransformation', date = '2018-12-29T07:49:34+03:00')
  static final class Interpolated extends ListTest {
    protected Interpolated(Context context, ListTest from) {
      super(
        ImmutableList.copyOf((List<InterpolableInteger>)from.@singleList.collect { InterpolableInteger interpolableItem -> interpolableItem.interpolateValue(context) }),
      )
    }
  }

  @Override
  @Generated(value = 'org.fidata.packer.engine.ast.AutoImplementAstTransformation', date = '2018-12-29T07:49:39+03:00')
  final ListTest interpolate(Context context) {
    return new Interpolated(context, this)
  }

  @Generated(value = 'org.fidata.packer.engine.ast.AutoImplementAstTransformation', date = '2018-12-29T07:49:45+03:00')
  static final void register(AbstractEngine engine) {
    engine.registerAbstractTypeMapping ListTest, Impl,ImmutableImpl
  }
}
