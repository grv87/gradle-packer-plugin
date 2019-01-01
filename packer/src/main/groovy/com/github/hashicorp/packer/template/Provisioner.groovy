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
 */
package com.github.hashicorp.packer.template

import com.fasterxml.jackson.annotation.JsonTypeInfo
import com.fasterxml.jackson.annotation.JsonUnwrapped
import org.fidata.packer.engine.AbstractEngine
import org.fidata.packer.engine.annotations.ExtraProcessed
import org.fidata.packer.engine.annotations.Timing
import org.fidata.packer.engine.exceptions.ObjectAlreadyInterpolatedForBuilderException
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
abstract class Provisioner<ThisClass extends Provisioner<ThisClass, P>, P extends Configuration> implements InterpolableObject<ThisClass> {
  final Class<P> configurationClass = (Class<P>)new TypeToken<P>(this.class) { }.rawType

  protected Provisioner() {
  }

  @JsonUnwrapped
  @ExtraProcessed
  abstract OnlyExcept getOnlyExcept()

  @Input // TODO
  abstract String getType()

  abstract static class Configuration<ThisClass extends Configuration> implements InterpolableObject<ThisClass> {
    protected Configuration() {
    }

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
  final protected void doInterpolate() { // MARK1
    configuration.interpolate context
    P overrideConfiguration = override[context.buildName]
    if (overrideConfiguration) {
      overrideConfiguration.interpolate(context)
      Class</*? extends Configuration*/ P> clazz = configurationClass
      while (true) {
        clazz.fields.each { Field field ->
          Object value = field.get(overrideConfiguration)
          if (value) {
            field.set this, value
          }
        }
        if (clazz == Configuration) {
          break
        }
        clazz = (Class<? extends Configuration>)clazz.superclass
      }
    }
  }

  final Provisioner interpolateForBuilder(Context buildCtx) {
    if (context.buildName) {
      throw new ObjectAlreadyInterpolatedForBuilderException()
    }
    // Stage 3
    if (onlyExcept == null || !onlyExcept.skip(buildCtx.buildName)) {
      Provisioner result = this.clone() // TODO
      result.interpolate buildCtx
      result
    } else {
      null
    }
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
