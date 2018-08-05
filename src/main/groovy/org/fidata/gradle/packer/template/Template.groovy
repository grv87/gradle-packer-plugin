/*
 * Template class
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

import com.fasterxml.jackson.annotation.JsonProperty
import groovy.transform.CompileStatic
import org.fidata.gradle.packer.template.internal.TemplateObject
import org.gradle.api.tasks.Console
import org.gradle.api.tasks.Internal
import org.gradle.api.tasks.Nested

@CompileStatic
class Template extends TemplateObject {
  @Console
  String description

  @JsonProperty('min_packer_version')
  @Internal
  String minVersion

  @Internal
  Map<String, String> variables

  @Nested
  /* TODO: Map<String, Builder> */ List<Builder> builders

  @Nested
  List<Provisioner> provisioners

  @JsonProperty('post-processors')
  @Nested
  List<Object> postProcessors

  @Override
  protected void doInterpolate(Context ctx) {
    for (Builder builder in builders) {
      ((TemplateObject)builder).interpolate(ctx)
    }
    for (Provisioner provisioner in provisioners) {
      ((TemplateObject)provisioner).interpolate(ctx)
    }
    for (Object object in postProcessors) {
      if (List.isInstance(object)) {
        for (PostProcessor postProcessor in (List<PostProcessor>)object) {
          ((TemplateObject)postProcessor).interpolate(ctx)
        }
      } else {
        ((TemplateObject) object).interpolate(ctx)
      }
    }
  }
}
