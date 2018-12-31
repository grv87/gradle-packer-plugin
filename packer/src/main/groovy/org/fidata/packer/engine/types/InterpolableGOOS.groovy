package org.fidata.packer.engine.types

import go.runtime.GOOS
import groovy.transform.CompileStatic
import groovy.transform.InheritConstructors
import groovy.transform.KnownImmutable
import org.fidata.packer.engine.types.base.InterpolableEnum

@CompileStatic
interface InterpolableGOOS extends InterpolableEnum<GOOS, InterpolableGOOS> {
  @KnownImmutable
  @InheritConstructors
  final class ImmutableRaw extends InterpolableEnum.ImmutableRaw<GOOS, InterpolableGOOS, Interpolated, AlreadyInterpolated> implements InterpolableGOOS { }

  @InheritConstructors
  final class Raw extends InterpolableEnum.Raw<GOOS, InterpolableGOOS, Interpolated, AlreadyInterpolated> implements InterpolableGOOS { }

  @InheritConstructors
  final class Interpolated extends InterpolableEnum.Interpolated<GOOS, InterpolableGOOS, AlreadyInterpolated> implements InterpolableGOOS { }

  @KnownImmutable
  @InheritConstructors
  final class AlreadyInterpolated extends InterpolableEnum.AlreadyInterpolated<GOOS, InterpolableGOOS> implements InterpolableGOOS { }
}
