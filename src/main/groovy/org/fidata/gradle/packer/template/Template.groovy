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

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.databind.ObjectMapper
import groovy.transform.CompileStatic
import org.fidata.gradle.packer.template.internal.InterpolableObject
import org.fidata.gradle.packer.template.types.InterpolableString
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
  Map<String, InterpolableString> variables

  @Nested
  /* TODO: Map<String, Builder> */ List<Builder> builders

  @Nested
  List<Provisioner> provisioners

  @JsonProperty('post-processors')
  @Nested
  List<PostProcessorArrayDefinition> postProcessors

  /*@JsonCreator
  Template() {
  }*/

  Context envContext

  @JsonIgnore
  @Internal
  Context getEnvContext() {
    this.envContext
  }

  Context variablesContext

  @JsonIgnore
  @Internal
  Context getVariablesContext() {
    this.variablesContext
  }

  @Override
  protected void doInterpolate() {
    super.doInterpolate() // TOTEST

    envContext = new Context(null, context.env, null, context.templateFile, context.task)
    variables.each.interpolate envContext

    Map<String, String> userVariables = (Map<String, String>)variables.collectEntries { Map.Entry<String, InterpolableString> entry ->
      [entry.key, context.userVariables.getOrDefault(entry.key, entry.value.interpolatedValue)]
    }
    Context variablesContext = new Context(userVariables, null, null, context.templateFile, context.task)
    for (Builder builder in builders) {
      builder.header.interpolate variablesContext
    }
  }

  Template interpolateBuilder(String buildName) {
    interpolate context
    Template result = new Template()
    Builder builder = builders.find { Builder builder -> builder.header.buildName == buildName }
    if (!builder) {
      throw new IllegalArgumentException(sprintf('Build with name `%s` not found.', [buildName]))
    }
    result.builders = [builder]
    Context buildCtx = variablesContext.addTemplateVariables([
      'BuildName': buildName,
      'BuilderType': builder.header.type,
    ])

    result.provisioners = new ArrayList()

    /*
    TODO
    for (Provisioner provisioner in provisioners) {
      if (!provisioner.onlyExcept?.skip(buildName)) {
        Provisioner clone = (Provisioner)(((InterpolableObject)provisioner).clone())
        ((InterpolableObject)clone).interpolate context
        result.provisioners.add clone
      }
    }

    for (Object object in postProcessors) {
      if (List.isInstance(object)) {
        for (PostProcessor postProcessor in (List<PostProcessor>)object) {
          // ((InterpolableObject)postProcessor).interpolate context
        }
      } else {
        ((InterpolableObject) object).interpolate context
      }
    }*/

    result
  }

  // @PackageScope
  static final ObjectMapper mapper = new ObjectMapper()
  static {
    mapper.registerModule(new AfterburnerModule())
    mapper.setPropertyNamingStrategy(PropertyNamingStrategy.SNAKE_CASE)
    mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
  }

  static Template readFromFile(File file) {
    (Template)file.withInputStream { InputStream inputStream ->
      mapper.readValue(inputStream, Template)
    }
  }
}
