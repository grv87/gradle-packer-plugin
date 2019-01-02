package org.fidata.aws.ec2

import com.amazonaws.services.ec2.model.InstanceType
import com.google.common.collect.ImmutableMap
import groovy.transform.CompileStatic

/**
 * Source: https://aws.amazon.com/ec2/instance-types/
 * Actual as of: 2018-12-24
 */
@CompileStatic
class InstanceTypeUtils {
  static final Map<InstanceType, Integer> NUMBER_OF_CPU_CORES = ImmutableMap.<InstanceType, Integer>builder()
    .put(InstanceType.A1Medium, 1)
    .put(InstanceType.A1Large, 2)
    .put(InstanceType.A1Xlarge, 4)
    .put(InstanceType.A12xlarge, 8)
    .put(InstanceType.A14xlarge, 16)
    .put(InstanceType.T3Nano, 2) // This is the default and maximum number of vCPUs available for this instance type. You can specify a custom number of vCPUs when launching this instance type
    .put(InstanceType.T3Micro, 2) // This is the default and maximum number of vCPUs available for this instance type. You can specify a custom number of vCPUs when launching this instance type
    .put(InstanceType.T3Small, 2) // This is the default and maximum number of vCPUs available for this instance type. You can specify a custom number of vCPUs when launching this instance type
    .put(InstanceType.T3Medium, 2) // This is the default and maximum number of vCPUs available for this instance type. You can specify a custom number of vCPUs when launching this instance type
    .put(InstanceType.T3Large, 2) // This is the default and maximum number of vCPUs available for this instance type. You can specify a custom number of vCPUs when launching this instance type
    .put(InstanceType.T3Xlarge, 4) // This is the default and maximum number of vCPUs available for this instance type. You can specify a custom number of vCPUs when launching this instance type
    .put(InstanceType.T32xlarge, 8) // This is the default and maximum number of vCPUs available for this instance type. You can specify a custom number of vCPUs when launching this instance type
    .put(InstanceType.T2Nano, 1) // This is the default and maximum number of vCPUs available for this instance type. You can specify a custom number of vCPUs when launching this instance type
    .put(InstanceType.T2Micro, 1) // This is the default and maximum number of vCPUs available for this instance type. You can specify a custom number of vCPUs when launching this instance type
    .put(InstanceType.T2Small, 1) // This is the default and maximum number of vCPUs available for this instance type. You can specify a custom number of vCPUs when launching this instance type
    .put(InstanceType.T2Medium, 2) // This is the default and maximum number of vCPUs available for this instance type. You can specify a custom number of vCPUs when launching this instance type
    .put(InstanceType.T2Large, 2) // This is the default and maximum number of vCPUs available for this instance type. You can specify a custom number of vCPUs when launching this instance type
    .put(InstanceType.T2Xlarge, 4) // This is the default and maximum number of vCPUs available for this instance type. You can specify a custom number of vCPUs when launching this instance type
    .put(InstanceType.T22xlarge, 8) // This is the default and maximum number of vCPUs available for this instance type. You can specify a custom number of vCPUs when launching this instance type
    .put(InstanceType.M5Large, 2) // This is the default and maximum number of vCPUs available for this instance type. You can specify a custom number of vCPUs when launching this instance type
    .put(InstanceType.M5Xlarge, 4) // This is the default and maximum number of vCPUs available for this instance type. You can specify a custom number of vCPUs when launching this instance type
    .put(InstanceType.M52xlarge, 8) // This is the default and maximum number of vCPUs available for this instance type. You can specify a custom number of vCPUs when launching this instance type
    .put(InstanceType.M54xlarge, 16) // This is the default and maximum number of vCPUs available for this instance type. You can specify a custom number of vCPUs when launching this instance type
    .put(InstanceType.M512xlarge, 48) // This is the default and maximum number of vCPUs available for this instance type. You can specify a custom number of vCPUs when launching this instance type
    .put(InstanceType.M524xlarge, 96) // This is the default and maximum number of vCPUs available for this instance type. You can specify a custom number of vCPUs when launching this instance type
    .put(InstanceType.M5dLarge, 2) // This is the default and maximum number of vCPUs available for this instance type. You can specify a custom number of vCPUs when launching this instance type
    .put(InstanceType.M5dXlarge, 4) // This is the default and maximum number of vCPUs available for this instance type. You can specify a custom number of vCPUs when launching this instance type
    .put(InstanceType.M5d2xlarge, 8) // This is the default and maximum number of vCPUs available for this instance type. You can specify a custom number of vCPUs when launching this instance type
    .put(InstanceType.M5d4xlarge, 16) // This is the default and maximum number of vCPUs available for this instance type. You can specify a custom number of vCPUs when launching this instance type
    .put(InstanceType.M5d12xlarge, 48) // This is the default and maximum number of vCPUs available for this instance type. You can specify a custom number of vCPUs when launching this instance type
    .put(InstanceType.M5d24xlarge, 96) // This is the default and maximum number of vCPUs available for this instance type. You can specify a custom number of vCPUs when launching this instance type
    .put(InstanceType.M5aLarge, 2) // This is the default and maximum number of vCPUs available for this instance type. You can specify a custom number of vCPUs when launching this instance type
    .put(InstanceType.M5aXlarge, 4) // This is the default and maximum number of vCPUs available for this instance type. You can specify a custom number of vCPUs when launching this instance type
    .put(InstanceType.M5a2xlarge, 8) // This is the default and maximum number of vCPUs available for this instance type. You can specify a custom number of vCPUs when launching this instance type
    .put(InstanceType.M5a4xlarge, 16) // This is the default and maximum number of vCPUs available for this instance type. You can specify a custom number of vCPUs when launching this instance type
    .put(InstanceType.M5a12xlarge, 48) // This is the default and maximum number of vCPUs available for this instance type. You can specify a custom number of vCPUs when launching this instance type
    .put(InstanceType.M5a24xlarge, 96) // This is the default and maximum number of vCPUs available for this instance type. You can specify a custom number of vCPUs when launching this instance type
    .put(InstanceType.M4Large, 2) // This is the default and maximum number of vCPUs available for this instance type. You can specify a custom number of vCPUs when launching this instance type
    .put(InstanceType.M4Xlarge, 4) // This is the default and maximum number of vCPUs available for this instance type. You can specify a custom number of vCPUs when launching this instance type
    .put(InstanceType.M42xlarge, 8) // This is the default and maximum number of vCPUs available for this instance type. You can specify a custom number of vCPUs when launching this instance type
    .put(InstanceType.M44xlarge, 16) // This is the default and maximum number of vCPUs available for this instance type. You can specify a custom number of vCPUs when launching this instance type
    .put(InstanceType.M410xlarge, 40) // This is the default and maximum number of vCPUs available for this instance type. You can specify a custom number of vCPUs when launching this instance type
    .put(InstanceType.M416xlarge, 64) // This is the default and maximum number of vCPUs available for this instance type. You can specify a custom number of vCPUs when launching this instance type
    // Coming soon
    // .put(InstanceType.T3Anano, 2) // This is the default and maximum number of vCPUs available for this instance type. You can specify a custom number of vCPUs when launching this instance type
    // .put(InstanceType.T3Amicro, 2) // This is the default and maximum number of vCPUs available for this instance type. You can specify a custom number of vCPUs when launching this instance type
    // .put(InstanceType.T3Asmall, 2) // This is the default and maximum number of vCPUs available for this instance type. You can specify a custom number of vCPUs when launching this instance type
    // .put(InstanceType.T3Amedium, 2) // This is the default and maximum number of vCPUs available for this instance type. You can specify a custom number of vCPUs when launching this instance type
    // .put(InstanceType.T3Alarge, 2) // This is the default and maximum number of vCPUs available for this instance type. You can specify a custom number of vCPUs when launching this instance type
    // .put(InstanceType.T3Axlarge, 4) // This is the default and maximum number of vCPUs available for this instance type. You can specify a custom number of vCPUs when launching this instance type
    // .put(InstanceType.T3A2Xlarge, 8) // This is the default and maximum number of vCPUs available for this instance type. You can specify a custom number of vCPUs when launching this instance type
    .put(InstanceType.C5Large, 2) // This is the default and maximum number of vCPUs available for this instance type. You can specify a custom number of vCPUs when launching this instance type
    .put(InstanceType.C5Xlarge, 4) // This is the default and maximum number of vCPUs available for this instance type. You can specify a custom number of vCPUs when launching this instance type
    .put(InstanceType.C52xlarge, 8) // This is the default and maximum number of vCPUs available for this instance type. You can specify a custom number of vCPUs when launching this instance type
    .put(InstanceType.C54xlarge, 16) // This is the default and maximum number of vCPUs available for this instance type. You can specify a custom number of vCPUs when launching this instance type
    .put(InstanceType.C59xlarge, 36) // This is the default and maximum number of vCPUs available for this instance type. You can specify a custom number of vCPUs when launching this instance type
    .put(InstanceType.C518xlarge, 72) // This is the default and maximum number of vCPUs available for this instance type. You can specify a custom number of vCPUs when launching this instance type
    .put(InstanceType.C5dLarge, 2) // This is the default and maximum number of vCPUs available for this instance type. You can specify a custom number of vCPUs when launching this instance type
    .put(InstanceType.C5dXlarge, 4) // This is the default and maximum number of vCPUs available for this instance type. You can specify a custom number of vCPUs when launching this instance type
    .put(InstanceType.C5d2xlarge, 8) // This is the default and maximum number of vCPUs available for this instance type. You can specify a custom number of vCPUs when launching this instance type
    .put(InstanceType.C5d4xlarge, 16) // This is the default and maximum number of vCPUs available for this instance type. You can specify a custom number of vCPUs when launching this instance type
    .put(InstanceType.C5d9xlarge, 36) // This is the default and maximum number of vCPUs available for this instance type. You can specify a custom number of vCPUs when launching this instance type
    .put(InstanceType.C5d18xlarge, 72) // This is the default and maximum number of vCPUs available for this instance type. You can specify a custom number of vCPUs when launching this instance type
    .put(InstanceType.C5nLarge, 2) // This is the default and maximum number of vCPUs available for this instance type. You can specify a custom number of vCPUs when launching this instance type
    .put(InstanceType.C5nXlarge, 4) // This is the default and maximum number of vCPUs available for this instance type. You can specify a custom number of vCPUs when launching this instance type
    .put(InstanceType.C5n2xlarge, 8) // This is the default and maximum number of vCPUs available for this instance type. You can specify a custom number of vCPUs when launching this instance type
    .put(InstanceType.C5n4xlarge, 16) // This is the default and maximum number of vCPUs available for this instance type. You can specify a custom number of vCPUs when launching this instance type
    .put(InstanceType.C5n9xlarge, 36) // This is the default and maximum number of vCPUs available for this instance type. You can specify a custom number of vCPUs when launching this instance type
    .put(InstanceType.C5n18xlarge, 72) // This is the default and maximum number of vCPUs available for this instance type. You can specify a custom number of vCPUs when launching this instance type
    .put(InstanceType.C4Large, 2) // This is the default and maximum number of vCPUs available for this instance type. You can specify a custom number of vCPUs when launching this instance type
    .put(InstanceType.C4Xlarge, 4) // This is the default and maximum number of vCPUs available for this instance type. You can specify a custom number of vCPUs when launching this instance type
    .put(InstanceType.C42xlarge, 8) // This is the default and maximum number of vCPUs available for this instance type. You can specify a custom number of vCPUs when launching this instance type
    .put(InstanceType.C44xlarge, 16) // This is the default and maximum number of vCPUs available for this instance type. You can specify a custom number of vCPUs when launching this instance type
    .put(InstanceType.C48xlarge, 36) // This is the default and maximum number of vCPUs available for this instance type. You can specify a custom number of vCPUs when launching this instance type
    .put(InstanceType.R5Large, 2)
    .put(InstanceType.R5Xlarge, 4)
    .put(InstanceType.R52xlarge, 8)
    .put(InstanceType.R54xlarge, 16)
    .put(InstanceType.R512xlarge, 48)
    .put(InstanceType.R524xlarge, 96)
    .put(InstanceType.R5dLarge, 2)
    .put(InstanceType.R5dXlarge, 4)
    .put(InstanceType.R5d2xlarge, 8)
    .put(InstanceType.R5d4xlarge, 16)
    .put(InstanceType.R5d12xlarge, 48)
    .put(InstanceType.R5d24xlarge, 96)
    .put(InstanceType.R5aLarge, 2)
    .put(InstanceType.R5aXlarge, 4)
    .put(InstanceType.R5a2xlarge, 8)
    .put(InstanceType.R5a4xlarge, 16)
    .put(InstanceType.R5a12xlarge, 48)
    .put(InstanceType.R5a24xlarge, 96)
    .put(InstanceType.R4Large, 2)
    .put(InstanceType.R4Xlarge, 4)
    .put(InstanceType.R42xlarge, 8)
    .put(InstanceType.R44xlarge, 16)
    .put(InstanceType.R48xlarge, 32)
    .put(InstanceType.R416xlarge, 64)
    .put(InstanceType.X1eXlarge, 4)
    .put(InstanceType.X1e2xlarge, 8)
    .put(InstanceType.X1e4xlarge, 16)
    .put(InstanceType.X1e8xlarge, 32)
    .put(InstanceType.X1e16xlarge, 64)
    .put(InstanceType.X1e32xlarge, 128)
    .put(InstanceType.X116xlarge, 64)
    .put(InstanceType.X132xlarge, 128)
    .put(InstanceType.U6tb1Metal, 448) // Each logical processor is a hyperthread on 224 cores
    .put(InstanceType.U9tb1Metal, 448) // Each logical processor is a hyperthread on 224 cores
    .put(InstanceType.U12tb1Metal, 448) // Each logical processor is a hyperthread on 224 cores
    .put(InstanceType.Z1dLarge, 2)
    .put(InstanceType.Z1dXlarge, 4)
    .put(InstanceType.Z1d2xlarge, 8)
    .put(InstanceType.Z1d3xlarge, 12)
    .put(InstanceType.Z1d6xlarge, 24)
    .put(InstanceType.Z1d12xlarge, 48)
    .put(InstanceType.G3sXlarge, 4)
    .put(InstanceType.G34xlarge, 16)
    .put(InstanceType.G38xlarge, 32)
    .put(InstanceType.G316xlarge, 64)
    .put(InstanceType.F12xlarge, 8)
    .put(InstanceType.F14xlarge, 16)
    .put(InstanceType.F116xlarge, 64)
    .put(InstanceType.P32xlarge, 8)
    .put(InstanceType.P38xlarge, 32)
    .put(InstanceType.P316xlarge, 64)
    .put(InstanceType.P3dn24xlarge, 96)
    .put(InstanceType.P2Xlarge, 4)
    .put(InstanceType.P28xlarge, 32)
    .put(InstanceType.P216xlarge, 64)
    .put(InstanceType.H12xlarge, 8) // This is the default and maximum number of vCPUs available for this instance type. You can specify a custom number of vCPUs when launching this instance type
    .put(InstanceType.H14xlarge, 16) // This is the default and maximum number of vCPUs available for this instance type. You can specify a custom number of vCPUs when launching this instance type
    .put(InstanceType.H18xlarge, 32) // This is the default and maximum number of vCPUs available for this instance type. You can specify a custom number of vCPUs when launching this instance type
    .put(InstanceType.H116xlarge, 64) // This is the default and maximum number of vCPUs available for this instance type. You can specify a custom number of vCPUs when launching this instance type
    .put(InstanceType.I3Large, 2) // This is the default and maximum number of vCPUs available for this instance type. You can specify a custom number of vCPUs when launching this instance type
    .put(InstanceType.I3Xlarge, 4) // This is the default and maximum number of vCPUs available for this instance type. You can specify a custom number of vCPUs when launching this instance type
    .put(InstanceType.I32xlarge, 8) // This is the default and maximum number of vCPUs available for this instance type. You can specify a custom number of vCPUs when launching this instance type
    .put(InstanceType.I34xlarge, 16) // This is the default and maximum number of vCPUs available for this instance type. You can specify a custom number of vCPUs when launching this instance type
    .put(InstanceType.I38xlarge, 32) // This is the default and maximum number of vCPUs available for this instance type. You can specify a custom number of vCPUs when launching this instance type
    .put(InstanceType.I316xlarge, 64) // This is the default and maximum number of vCPUs available for this instance type. You can specify a custom number of vCPUs when launching this instance type
    .put(InstanceType.I3Metal, 72) // This is the default and maximum number of vCPUs available for this instance type. You can specify a custom number of vCPUs when launching this instance type // 72 logical processors on 36 physical cores
    .put(InstanceType.D2Xlarge, 4) // This is the default and maximum number of vCPUs available for this instance type. You can specify a custom number of vCPUs when launching this instance type
    .put(InstanceType.D22xlarge, 8) // This is the default and maximum number of vCPUs available for this instance type. You can specify a custom number of vCPUs when launching this instance type
    .put(InstanceType.D24xlarge, 16) // This is the default and maximum number of vCPUs available for this instance type. You can specify a custom number of vCPUs when launching this instance type
    .put(InstanceType.D28xlarge, 36) // This is the default and maximum number of vCPUs available for this instance type. You can specify a custom number of vCPUs when launching this instance type
    .build()

  private InstanceTypeUtils() {}
}
