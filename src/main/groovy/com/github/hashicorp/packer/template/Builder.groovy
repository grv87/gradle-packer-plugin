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

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonTypeInfo
import com.fasterxml.jackson.databind.jsontype.NamedType
import groovy.transform.AutoClone
import groovy.transform.AutoCloneStyle
import groovy.transform.CompileStatic
import com.github.hashicorp.packer.template.annotations.Inline
import com.github.hashicorp.packer.common.types.internal.InterpolableObject
import com.github.hashicorp.packer.template.types.InterpolableString
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.Internal

@AutoClone(style = AutoCloneStyle.SIMPLE)
@JsonTypeInfo(
  use = JsonTypeInfo.Id.NAME,
  include = JsonTypeInfo.As.PROPERTY,
  property = 'type'
)
@CompileStatic
class Builder extends InterpolableObject {
  protected Builder() {
  }

  @Inline
  BuilderHeader header

  @Override
  protected void doInterpolate() {
    header.interpolate context
  }

  static final class BuilderHeader extends InterpolableObject {
    @Internal
    InterpolableString name

    @Input
    String type

    @JsonIgnore
    @Input
    String getBuildName() {
      name?.interpolatedValue ?: type
    }

    @Override
    protected void doInterpolate() {
      name.interpolate context
    }
  }

  static void registerSubtype(String type, Class<? extends Builder> aClass) {
    Template.MAPPER.registerSubtypes(new NamedType(aClass, type))
  }
}
