package com.github.hashicorp.packer.builder.amazon.common

import com.github.hashicorp.packer.engine.types.base.InterpolableEnum
import groovy.transform.InheritConstructors

interface InterpolableVolumeType extends InterpolableEnum<VolumeType, InterpolableVolumeType> {
  @InheritConstructors
  final class ImmutableRaw extends InterpolableEnum.ImmutableRaw<VolumeType, InterpolableVolumeType, Interpolated, AlreadyInterpolated> implements InterpolableVolumeType { }

  @InheritConstructors
  final class Raw extends InterpolableEnum.Raw<VolumeType, InterpolableVolumeType, Interpolated, AlreadyInterpolated> implements InterpolableVolumeType { }

  @InheritConstructors
  final class Interpolated extends InterpolableEnum.Interpolated<VolumeType, InterpolableVolumeType, AlreadyInterpolated> implements InterpolableVolumeType { }

  @InheritConstructors
  final class AlreadyInterpolated extends InterpolableEnum.AlreadyInterpolated<VolumeType, InterpolableVolumeType> implements InterpolableVolumeType { }
}
