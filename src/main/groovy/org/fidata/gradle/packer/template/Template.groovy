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

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.databind.ObjectMapper
import groovy.transform.CompileStatic
import org.fidata.gradle.packer.template.internal.InterpolableObject
import org.fidata.gradle.packer.template.utils.PostProcessorArrayDefinition
import org.gradle.api.tasks.Console
import org.gradle.api.tasks.Internal
import org.gradle.api.tasks.Nested
import com.fasterxml.jackson.databind.PropertyNamingStrategy
import com.fasterxml.jackson.module.afterburner.AfterburnerModule

@CompileStatic
class Template extends InterpolableObject {
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
  List<PostProcessorArrayDefinition> postProcessors

  @Override
  protected void doInterpolate(Context ctx) {
    super.doInterpolate ctx // TOTEST
    for (Builder builder in builders) {
      builder.header.interpolate ctx
    }
  }

  Template interpolateBuilder(Context ctx, String builderName) {
    /*Template result = new Template()
    interpolate ctx
    Builder builder = builders.find { Builder builder -> builder.header.interpolatedName == builderName }
    if (!builder) {
      throw new IllegalArgumentException(sprintf('Builder with name `%s` not found.', [builderName]))
    }
    result.builders = [builder]

    // ((InterpolableObject)builder).interpolate ctx

    ctx.buildType = builder.header.type
    ctx.buildName = builder.header.interpolatedName

    result.provisioners = new ArrayList()

    for (Provisioner provisioner in provisioners) {
      if (!provisioner.onlyExcept?.skip(builderName)) {
        Provisioner clone = (Provisioner)(((InterpolableObject)provisioner).clone())
        ((InterpolableObject)clone).interpolate ctx
        result.provisioners.add clone
      }
    }

    for (Object object in postProcessors) {
      if (List.isInstance(object)) {
        for (PostProcessor postProcessor in (List<PostProcessor>)object) {
          // ((InterpolableObject)postProcessor).interpolate ctx
        }
      } else {
        ((InterpolableObject) object).interpolate ctx
      }
    }*/
  }

  // @PackageScope
  static final ObjectMapper mapper = new ObjectMapper()
  static {
    mapper.registerModule(new AfterburnerModule())
    mapper.setPropertyNamingStrategy(PropertyNamingStrategy.SNAKE_CASE)
    mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
  }

  static Template readFromFile(File file) {
    Template template
    file.withInputStream { InputStream inputStream ->
      template = mapper.readValue(inputStream, Template)
    }
    template
  }
}
