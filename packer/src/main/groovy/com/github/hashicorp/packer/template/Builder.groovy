/*
 * Builder class
 * Copyright ©  Basil Peace
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
 *
 * Ported from original Packer code,
 * file template/template.go
 * under the terms of the Mozilla Public License, v. 2.0.
 */
package com.github.hashicorp.packer.template

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonTypeInfo
import org.fidata.packer.engine.AbstractEngine
import org.fidata.packer.engine.BuilderResult
import org.fidata.packer.engine.annotations.AutoImplement
import groovy.transform.CompileStatic
import org.fidata.packer.engine.annotations.ExtraProcessed
import org.fidata.packer.engine.annotations.Inline
import org.fidata.packer.engine.types.base.InterpolableObject
import org.fidata.packer.engine.types.InterpolableString
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.Internal

@JsonTypeInfo(
  use = JsonTypeInfo.Id.NAME,
  include = JsonTypeInfo.As.PROPERTY,
  property = 'type'
)
@AutoImplement
@CompileStatic
abstract class Builder<ThisClass extends Builder<ThisClass>> implements InterpolableObject<ThisClass> {
  protected Builder() {
  }

  @Inline
  abstract BuilderHeader getHeader()

  final BuilderResult run() {
    // Stage 4
    doRun()
  }

  /**
   * Run is where the actual build should take place
   *
   * Implementations should emulate in this method the run of builder
   * and returning information required to configure Gradle task.
   *
   * @return Run result
   */
  protected abstract BuilderResult doRun()

  @AutoImplement
  abstract static class BuilderHeader implements InterpolableObject<BuilderHeader> {
    @ExtraProcessed
    abstract InterpolableString getName()

    @Input
    abstract String getType()

    @JsonIgnore
    @Internal
    String getBuildName() {
      name?.interpolated ?: type
    }
  }

  static void register(AbstractEngine engine) {
    engine.addSubtypeRegistry Builder
  }
}
