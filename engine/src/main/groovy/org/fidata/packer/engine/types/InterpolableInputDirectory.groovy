package org.fidata.packer.engine.types

import org.fidata.packer.engine.annotations.ComputedInputDirectory
import groovy.transform.CompileStatic
import groovy.transform.InheritConstructors
import groovy.transform.KnownImmutable

// This class is required to overcome the fact that Gradle doesn't have InputDirectories annotation
@CompileStatic
interface InterpolableInputDirectory extends InterpolableFile<InterpolableInputDirectory> {
  @KnownImmutable
  @InheritConstructors
  final class ImmutableRaw extends InterpolableFile.ImmutableRaw<InterpolableInputDirectory, Interpolated, AlreadyInterpolated> implements InterpolableInputDirectory { }

  @InheritConstructors
  final class Raw extends InterpolableFile.Raw<InterpolableInputDirectory, Interpolated, AlreadyInterpolated> implements InterpolableInputDirectory { }

  @InheritConstructors
  final class Interpolated extends InterpolableFile.Interpolated<InterpolableInputDirectory, AlreadyInterpolated> implements InterpolableInputDirectory {
    @ComputedInputDirectory
    File getInputDirectory() {
      interpolated
    }
  }

  @KnownImmutable
  @InheritConstructors
  final class AlreadyInterpolated extends InterpolableFile.AlreadyInterpolated<InterpolableInputDirectory> implements InterpolableInputDirectory {
    @ComputedInputDirectory
    File getInputDirectory() {
      interpolated
    }
  }
}
