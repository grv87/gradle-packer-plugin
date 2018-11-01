/*
 * Provisioner class
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
import com.fasterxml.jackson.annotation.JsonUnwrapped
import com.fasterxml.jackson.databind.jsontype.NamedType
import com.github.hashicorp.packer.engine.exceptions.ObjectAlreadyInterpolatedForBuilder
import groovy.transform.AutoClone
import groovy.transform.AutoCloneStyle
import groovy.transform.CompileStatic
import com.github.hashicorp.packer.engine.annotations.Inline
import com.github.hashicorp.packer.engine.types.InterpolableObject
import com.github.hashicorp.packer.engine.types.InterpolableDuration
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.Internal

import java.lang.reflect.Field

@AutoClone(style = AutoCloneStyle.SIMPLE)
@JsonTypeInfo(
  use = JsonTypeInfo.Id.NAME,
  include = JsonTypeInfo.As.PROPERTY,
  property = 'type'
)
@CompileStatic

class Provisioner<P extends Configuration> extends InterpolableObject {
  final Class<? extends Configuration> configurationClass

  protected Provisioner(Class<? extends Configuration> configurationClass) {
    this.configurationClass = configurationClass
  }

  @JsonUnwrapped
  @Internal
  OnlyExcept onlyExcept

  @Input // TODO
  String type

  static class Configuration extends InterpolableObject {
    protected Configuration() {
    }

    @Internal
    InterpolableDuration pauseBefore

    @Override
    protected void doInterpolate() {
      pauseBefore.interpolate context
    }
  }

  @Internal
  Map<String, P> override

  @Inline
  P configuration

  /**
   * Interpolates instance of the provisioner for specific builder.
   * * Copies `override` configuration
   */
  @Override
  final protected void doInterpolate() {
    configuration.interpolate context
    P overrideConfiguration = override[context.buildName]
    if (overrideConfiguration) {
      overrideConfiguration.interpolate(context)
      Class<? extends Configuration> clazz = configurationClass
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
      throw new ObjectAlreadyInterpolatedForBuilder()
    }
    // Stage 3
    if (onlyExcept == null || !onlyExcept.skip(buildCtx.buildName)) {
      Provisioner result = this.clone()
      result.interpolate buildCtx
      result
    } else {
      null
    }
  }

  static void registerSubtype(String type, Class<? extends Provisioner> clazz) {
    Template.MAPPER.registerSubtypes(new NamedType(clazz, type))
  }
}
