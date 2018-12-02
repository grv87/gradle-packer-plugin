package com.github.hashicorp.packer.engine.types

import com.github.hashicorp.packer.engine.enums.ChecksumType
import groovy.transform.CompileStatic
import groovy.transform.InheritConstructors

@CompileStatic
interface InterpolableChecksumType extends InterpolableEnum<ChecksumType> {
@InheritConstructors
  final class ImmutableRaw extends InterpolableEnum.ImmutableRaw<ChecksumType> implements InterpolableChecksumType { }

  @InheritConstructors
  final class Raw extends InterpolableEnum.ImmutableRaw<ChecksumType> implements InterpolableChecksumType { }

  @InheritConstructors
  final class Interpolated extends InterpolableEnum.ImmutableRaw<ChecksumType> implements InterpolableChecksumType { }

  @InheritConstructors
  final class AlreadyInterpolated extends InterpolableEnum.ImmutableRaw<ChecksumType> implements InterpolableChecksumType { }
}
