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
package org.fidata.gradle.packer.template

import com.fasterxml.jackson.annotation.JsonSubTypes
import com.fasterxml.jackson.annotation.JsonTypeInfo
import com.fasterxml.jackson.annotation.JsonUnwrapped
import groovy.transform.CompileStatic
import org.fidata.gradle.packer.template.internal.InterpolableObject
import org.fidata.gradle.packer.template.provisioner.File
import org.fidata.gradle.packer.template.types.InterpolableString
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.Internal

import java.time.Duration

@JsonTypeInfo(
  use = JsonTypeInfo.Id.NAME,
  include = JsonTypeInfo.As.PROPERTY,
  property = 'type'
)
@JsonSubTypes([
  @JsonSubTypes.Type(name = 'file', value = File),
])
@CompileStatic
abstract class Provisioner extends InterpolableObject {
  @Internal // TODO
  @JsonUnwrapped
  OnlyExcept onlyExcept

  @Input // TODO
  String type

  @Internal
  Map<InterpolableString, Object /* TODO */> override

  @Internal
  Duration pauseBefore // TODO: Write parser

  @Override
  protected void doInterpolate(Context ctx) {
    /*for (Map.Entry<InterpolableString, Object>
      InterpolableString buildName, Provisioner provisioner : override) {
      TODO: apply override
    }*/
  }
}
