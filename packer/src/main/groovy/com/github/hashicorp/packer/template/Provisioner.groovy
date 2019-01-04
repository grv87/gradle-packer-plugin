/*
 * Provisioner class
 * Copyright © 2018-2019  Basil Peace
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

import com.fasterxml.jackson.annotation.JsonTypeInfo
import com.fasterxml.jackson.annotation.JsonUnwrapped
import org.fidata.packer.engine.AbstractEngine
import org.fidata.packer.engine.annotations.ExtraProcessed
import org.fidata.packer.engine.annotations.Timing
import groovy.transform.CompileStatic
import org.fidata.packer.engine.annotations.Inline
import org.fidata.packer.engine.types.base.InterpolableObject
import org.fidata.packer.engine.types.InterpolableDuration
import org.gradle.api.tasks.Input
import com.google.common.reflect.TypeToken
import java.lang.reflect.Field

@JsonTypeInfo(
  use = JsonTypeInfo.Id.NAME,
  include = JsonTypeInfo.As.PROPERTY,
  property = 'type'
)
@CompileStatic
abstract class Provisioner<ThisClass extends Provisioner<ThisClass, P>, P extends Config> implements InterpolableObject<ThisClass> {
  final Class<P> configClass = (Class<P>)new TypeToken<P>(this.class) { }.rawType

  @JsonUnwrapped
  @ExtraProcessed
  abstract OnlyExcept getOnlyExcept()

  @Input // TODO
  abstract String getType()

  abstract static class Config<ThisClass extends Config<ThisClass>> implements InterpolableObject<ThisClass> {
    @Timing
    abstract InterpolableDuration getPauseBefore()
  }

  @ExtraProcessed
  abstract Map<String, P> getOverride()

  @Inline
  abstract P getConfiguration()

  /**
   * Interpolates instance of the provisioner for specific builder.
   * * Copies `override` configuration
   */
  @Override
  final ThisClass interpolate(Context context) { // MARK1
    configuration.interpolate context
    P overrideConfiguration = override[context.buildName]
    if (overrideConfiguration) {
      overrideConfiguration.interpolate(context)
      Class</*? extends Config*/ P> clazz = configClass
      while (true) {
        clazz.fields.each { Field field ->
          Object value = field.get(overrideConfiguration)
          if (value) {
            field.set this, value
          }
        }
        if (clazz == Config) {
          break
        }
        clazz = (Class<? extends Config>)clazz.superclass
      }
    }
  }

  final Provisioner interpolateForBuilder(AbstractEngine engine, Context buildCtx) {
    // Stage 3
    if (onlyExcept == null || !onlyExcept.skip(buildCtx.buildName)) {
      interpolate(buildCtx)
    } else {
      null
    }
  }

  final static class Interpolated {
    Interpolated(

    )
  }

  /**
   * Registers this class in specified Engine
   *
   * @param engine Engine to register in
   */
  static void register(AbstractEngine engine) {
    engine.addSubtypeRegistry Provisioner
  }
}
