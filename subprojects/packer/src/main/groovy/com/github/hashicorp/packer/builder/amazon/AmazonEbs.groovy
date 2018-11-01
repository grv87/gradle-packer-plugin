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

import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.annotation.JsonUnwrapped
import com.github.hashicorp.packer.builder.amazon.common.AMIConfig
import com.github.hashicorp.packer.builder.amazon.common.AccessConfig
import com.github.hashicorp.packer.builder.amazon.common.BlockDevices
import com.github.hashicorp.packer.builder.amazon.common.RunConfig
import com.github.hashicorp.packer.builder.amazon.common.TagMap
import com.github.hashicorp.packer.engine.annotations.Inline
import groovy.transform.AutoClone
import groovy.transform.AutoCloneStyle
import groovy.transform.CompileStatic
import com.github.hashicorp.packer.template.Builder
import org.gradle.api.tasks.Internal

@AutoClone(style = AutoCloneStyle.SIMPLE)
@CompileStatic
class AmazonEbs extends Builder {
  @Inline
  AccessConfig accessConfig

  @Inline
  AMIConfig amiConfig

  @Inline
  BlockDevices blockDevices

  @Inline
  RunConfig runConfig

  @JsonProperty('run_volume_tags')
  @Internal
  TagMap volumeRunTags
}
