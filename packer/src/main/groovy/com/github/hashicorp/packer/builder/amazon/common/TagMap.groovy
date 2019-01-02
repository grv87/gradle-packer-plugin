package com.github.hashicorp.packer.builder.amazon.common

import com.fasterxml.jackson.annotation.JsonCreator
import org.fidata.packer.engine.types.InterpolableString
import org.fidata.packer.engine.types.base.InterpolableValue
import com.github.hashicorp.packer.template.Context
import com.google.common.collect.ImmutableMap
import groovy.transform.CompileStatic
import groovy.transform.InheritConstructors
import groovy.transform.KnownImmutable

@CompileStatic
interface TagMap extends InterpolableValue<Map<InterpolableString, InterpolableString>, ImmutableMap<String, String>, TagMap> {
  @InheritConstructors
  static final class MapClass extends HashMap<InterpolableString, InterpolableString> { }

  @KnownImmutable
  final class ImmutableRaw extends InterpolableValue.ImmutableRaw<Map<InterpolableString, InterpolableString>, ImmutableMap<String, String>, TagMap, Interpolated, AlreadyInterpolated> implements TagMap {
    ImmutableRaw() {
      super() // TODO: empty map ?
    }

    @JsonCreator(mode = JsonCreator.Mode.DELEGATING)
    ImmutableRaw(MapClass raw) {
      super(ImmutableMap.copyOf(raw))
    }

    protected static final ImmutableMap<String, String> doInterpolatePrimitive(Context context, Map<InterpolableString, InterpolableString> raw) {
      ImmutableMap.copyOf((Map<String, String>)raw.collectEntries { InterpolableString key, InterpolableString value ->
        [(key.interpolateValue(context)): value.interpolateValue(context)]
      })
    }
  }

  final class Raw extends InterpolableValue.Raw<Map<InterpolableString, InterpolableString>, ImmutableMap<String, String>, TagMap, Interpolated, AlreadyInterpolated> implements TagMap {
    Raw() {
      super() // TODO: empty map ?
    }

    @JsonCreator(mode = JsonCreator.Mode.DELEGATING)
    Raw(MapClass raw) {
      super(new ArrayList(raw))
    }

    protected static final ImmutableMap<String, String> doInterpolatePrimitive(Context context, Map<InterpolableString, InterpolableString> raw) {
      ImmutableMap.copyOf((Map<String, String>)raw.collectEntries { InterpolableString key, InterpolableString value ->
        [(key.interpolateValue(context)): value.interpolateValue(context)]
      })
    }
  }

  @InheritConstructors
  final class Interpolated extends InterpolableValue.Interpolated<Map<InterpolableString, InterpolableString>, ImmutableMap<String, String>, TagMap, AlreadyInterpolated> implements TagMap { }

  @KnownImmutable
  @InheritConstructors
  final class AlreadyInterpolated extends InterpolableValue.AlreadyInterpolated<Map<InterpolableString, InterpolableString>, ImmutableMap<String, String>, TagMap> implements TagMap { }
}
