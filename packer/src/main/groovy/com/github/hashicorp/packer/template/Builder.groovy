/*
 * Builder class
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
package com.github.hashicorp.packer.template

import com.fasterxml.jackson.annotation.JsonTypeInfo
import com.github.hashicorp.packer.engine.Engine
import com.github.hashicorp.packer.engine.annotations.AutoImplement
import com.github.hashicorp.packer.engine.annotations.ComputedInput
import com.github.hashicorp.packer.engine.SubtypeRegistry
import com.github.hashicorp.packer.packer.Artifact
import groovy.transform.CompileStatic
import com.github.hashicorp.packer.engine.annotations.Inline
import com.github.hashicorp.packer.engine.types.base.InterpolableObject
import com.github.hashicorp.packer.engine.types.InterpolableString
import org.gradle.api.provider.Provider
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.Internal

@JsonTypeInfo(
  use = JsonTypeInfo.Id.NAME,
  include = JsonTypeInfo.As.PROPERTY,
  property = 'type'
)
@AutoImplement
@CompileStatic
// REVIEWED
abstract class Builder implements InterpolableObject<Builder> {
  protected Builder() {
  }

  @Inline
  abstract BuilderHeader getHeader()

  final Tuple2<Artifact, List<Provider<Boolean>>> run() {
    // Stage 4
    doRun()
  }

  protected abstract Tuple2<Artifact, List<Provider<Boolean>>> doRun()

  /**
   * Number of local CPUs used for build.
   *
   * For cloud builders, if the builder is able to detect
   * that the cloud is run alongside with this build on the same host
   * (e.g. local installation of Eucalyptus)
   * then it should treat used CPUs as local
   * and so return their number from this method.
   * Otherwise, it should return zero.
   *
   * Right now this number is not used for anything.
   * In the future it could be used to limit build parallelism.
   *
   * @return Number of local CPUs used for build
   */
  @Internal
  abstract int getLocalCpusUsed()

  @AutoImplement
  abstract static class BuilderHeader implements InterpolableObject<BuilderHeader> {
    @Internal
    abstract InterpolableString getName()

    @Input
    abstract String getType()

    @ComputedInput
    String getBuildName() {
      name?.interpolated ?: type
    }
  }

  protected static final SubtypeRegistry<Builder> SUBTYPE_REGISTRY = new SubtypeRegistry<Builder>()

  static void register(Engine engine) {
    engine.registerCustomModuleProvider SUBTYPE_REGISTRY
  }
}
