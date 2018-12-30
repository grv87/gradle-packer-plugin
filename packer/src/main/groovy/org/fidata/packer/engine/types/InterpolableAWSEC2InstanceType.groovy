package org.fidata.packer.engine.types

import com.amazonaws.services.ec2.model.InstanceType
import groovy.transform.CompileStatic
import groovy.transform.InheritConstructors
import groovy.transform.KnownImmutable
import org.fidata.packer.engine.types.base.InterpolableEnum

@CompileStatic
interface InterpolableAWSEC2InstanceType extends InterpolableEnum<InstanceType, InterpolableAWSEC2InstanceType> {
  @KnownImmutable
  @InheritConstructors
  final class ImmutableRaw extends InterpolableEnum.ImmutableRaw<InstanceType, InterpolableAWSEC2InstanceType, Interpolated, AlreadyInterpolated> implements InterpolableAWSEC2InstanceType { }

  @InheritConstructors
  final class Raw extends InterpolableEnum.Raw<InstanceType, InterpolableAWSEC2InstanceType, Interpolated, AlreadyInterpolated> implements InterpolableAWSEC2InstanceType { }

  @InheritConstructors
  final class Interpolated extends InterpolableEnum.Interpolated<InstanceType, InterpolableAWSEC2InstanceType, AlreadyInterpolated> implements InterpolableAWSEC2InstanceType { }

  @KnownImmutable
  @InheritConstructors
  final class AlreadyInterpolated extends InterpolableEnum.AlreadyInterpolated<InstanceType, InterpolableAWSEC2InstanceType> implements InterpolableAWSEC2InstanceType { }
}
