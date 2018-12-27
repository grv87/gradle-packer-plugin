package org.fidata.packer.engine.types

import org.fidata.packer.engine.types.base.InterpolableEnum
import com.github.hashicorp.packer.enums.VBoxGuestAdditionsMode
import groovy.transform.CompileStatic
import groovy.transform.InheritConstructors
import groovy.transform.KnownImmutable

@CompileStatic
interface InterpolableVBoxGuestAdditionsMode extends InterpolableEnum<VBoxGuestAdditionsMode, InterpolableVBoxGuestAdditionsMode> {
  @KnownImmutable
  @InheritConstructors
  final class ImmutableRaw extends InterpolableEnum.ImmutableRaw<VBoxGuestAdditionsMode, InterpolableVBoxGuestAdditionsMode, Interpolated, AlreadyInterpolated> implements InterpolableVBoxGuestAdditionsMode { }

  @InheritConstructors
  final class Raw extends InterpolableEnum.Raw<VBoxGuestAdditionsMode, InterpolableVBoxGuestAdditionsMode, Interpolated, AlreadyInterpolated> implements InterpolableVBoxGuestAdditionsMode { }

  @InheritConstructors
  final class Interpolated extends InterpolableEnum.Interpolated<VBoxGuestAdditionsMode, InterpolableVBoxGuestAdditionsMode, AlreadyInterpolated> implements InterpolableVBoxGuestAdditionsMode { }

  @KnownImmutable
  @InheritConstructors
  final class AlreadyInterpolated extends InterpolableEnum.AlreadyInterpolated<VBoxGuestAdditionsMode, InterpolableVBoxGuestAdditionsMode> implements InterpolableVBoxGuestAdditionsMode { }
}
