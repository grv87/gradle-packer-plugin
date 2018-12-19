package com.github.hashicorp.packer.engine.types


import com.github.hashicorp.packer.engine.types.base.InterpolableEnum
import com.github.hashicorp.packer.enums.ChecksumType
import groovy.transform.CompileStatic
import groovy.transform.InheritConstructors

@CompileStatic
interface InterpolableChecksumType extends InterpolableEnum<ChecksumType, InterpolableChecksumType> {
@InheritConstructors
  final class ImmutableRaw extends InterpolableEnum.ImmutableRaw<ChecksumType, InterpolableChecksumType, Interpolated, AlreadyInterpolated> implements InterpolableChecksumType { }

  @InheritConstructors
  final class Raw extends InterpolableEnum.Raw<ChecksumType, InterpolableChecksumType, Interpolated, AlreadyInterpolated> implements InterpolableChecksumType { }

  @InheritConstructors
  final class Interpolated extends InterpolableEnum.Interpolated<ChecksumType, InterpolableChecksumType, AlreadyInterpolated> implements InterpolableChecksumType { }

  @InheritConstructors
  final class AlreadyInterpolated extends InterpolableEnum.AlreadyInterpolated<ChecksumType, InterpolableChecksumType> implements InterpolableChecksumType { }
}
