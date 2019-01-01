/*
 * AmazonEbs class
 * Copyright © 2018  Basil Peace
 *
 * This file is part of gradle-packer-plugin.
 *
 * This plugin is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License
 * as published by the Free Software Foundation, either version 3
 * of the License, or (at your option) any later version.
 *
 * This plugin is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this plugin.  If not, see <https://www.gnu.org/licenses/>.
 */
package com.github.hashicorp.packer.builder.amazon

import static org.fidata.utils.InetAddressUtils.isLocalHost
import org.fidata.packer.engine.AbstractEngine
import org.fidata.packer.engine.annotations.LaunchedVMConfiguration
import com.fasterxml.jackson.annotation.JsonProperty
import com.github.hashicorp.packer.builder.amazon.common.AMIConfig
import com.github.hashicorp.packer.builder.amazon.common.AccessConfig
import com.github.hashicorp.packer.builder.amazon.common.BlockDevices
import com.github.hashicorp.packer.builder.amazon.common.RunConfig
import com.github.hashicorp.packer.builder.amazon.common.TagMap
import org.fidata.packer.engine.annotations.AutoImplement
import org.fidata.packer.engine.annotations.Inline
import com.github.hashicorp.packer.packer.Artifact
import groovy.transform.CompileStatic
import com.github.hashicorp.packer.template.Builder
import org.fidata.aws.ec2.InstanceTypeUtils
import org.gradle.api.provider.Provider

@CompileStatic
@AutoImplement
abstract class AmazonEbs extends Builder<AmazonEbs> {
  @Inline
  abstract AccessConfig getAccessConfig()

  @Inline
  abstract AMIConfig getAmiConfig()

  @Inline
  abstract BlockDevices getBlockDevices()

  @Inline
  abstract RunConfig getRunConfig()

  @JsonProperty('run_volume_tags')
  @LaunchedVMConfiguration
  abstract TagMap getVolumeRunTags()

  @Override
  protected final Tuple2<Artifact, List<Provider<Boolean>>> doRun() {
    // TODO
  }

  @Override
  final int getLocalCpusUsed() {
    // TODO: null customEndpointEc2 ?
    // TOTEST: if this doesn't work with Eucalyptus then this makes no sense
    isLocalHost(accessConfig.customEndpointEc2.interpolated) ? InstanceTypeUtils.NUMBER_OF_CPU_CORES[runConfig.instanceType.interpolated] : 0
  }

  static void register(AbstractEngine engine) {
    engine.registerSubtype Builder, 'amazon-ebs', this
  }
}
