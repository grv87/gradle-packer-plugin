package com.github.hashicorp.packer.engine.types

import com.github.hashicorp.packer.engine.enums.VBoxGuestAdditionsMode
import groovy.transform.CompileStatic
import groovy.transform.InheritConstructors

@CompileStatic
interface InterpolableVBoxGuestAdditionsMode extends InterpolableEnum<VBoxGuestAdditionsMode, InterpolableVBoxGuestAdditionsMode> {
  @InheritConstructors
  final class ImmutableRaw extends InterpolableEnum.ImmutableRaw<VBoxGuestAdditionsMode, InterpolableVBoxGuestAdditionsMode> implements InterpolableVBoxGuestAdditionsMode { }

  @InheritConstructors
  final class Raw extends InterpolableEnum.ImmutableRaw<VBoxGuestAdditionsMode, InterpolableVBoxGuestAdditionsMode> implements InterpolableVBoxGuestAdditionsMode { }

  @InheritConstructors
  final class Interpolated extends InterpolableEnum.ImmutableRaw<VBoxGuestAdditionsMode, InterpolableVBoxGuestAdditionsMode> implements InterpolableVBoxGuestAdditionsMode { }

  @InheritConstructors
  final class AlreadyInterpolated extends InterpolableEnum.ImmutableRaw<VBoxGuestAdditionsMode, InterpolableVBoxGuestAdditionsMode> implements InterpolableVBoxGuestAdditionsMode { }
}
