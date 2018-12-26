package com.github.hashicorp.packer.engine.types

import com.github.hashicorp.packer.engine.annotations.ComputedInputFile
import com.github.hashicorp.packer.engine.annotations.ComputedInternal
import com.github.hashicorp.packer.engine.exceptions.ValueNotInterpolatedYetException
import groovy.transform.CompileStatic
import groovy.transform.InheritConstructors
import groovy.transform.KnownImmutable
import org.gradle.api.tasks.Optional

@CompileStatic
interface InterpolableInputURI extends InterpolableURI<InterpolableInputURI> {
  // TOTEST
  @ComputedInputFile
  @Optional
  @Override
  URI getFileURI() // TODO: RegularFile ?

  @ComputedInternal
  @Override
  URI getNonFileURI()

  @KnownImmutable
  @InheritConstructors
  final class ImmutableRaw extends InterpolableURI.ImmutableRaw<InterpolableInputURI, Interpolated, AlreadyInterpolated> implements InterpolableInputURI {}

  @InheritConstructors
  final class Raw extends InterpolableURI.Raw<InterpolableInputURI, Interpolated, AlreadyInterpolated> implements InterpolableInputURI {}

  @InheritConstructors
  final class Interpolated extends InterpolableURI.Interpolated<InterpolableInputURI, AlreadyInterpolated> implements InterpolableInputURI {}

  @KnownImmutable
  @InheritConstructors
  final class AlreadyInterpolated extends InterpolableURI.AlreadyInterpolated<InterpolableInputURI> implements InterpolableInputURI {}
}
