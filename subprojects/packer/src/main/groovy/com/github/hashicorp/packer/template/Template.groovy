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
package com.github.hashicorp.packer.template

import com.github.hashicorp.packer.engine.exceptions.ObjectAlreadyInterpolatedForBuilder
import com.github.hashicorp.packer.packer.Artifact
import org.gradle.api.provider.Provider
import static Context.BUILD_NAME_VARIABLE_NAME
import org.gradle.api.file.ProjectLayout
import javax.inject.Inject
import groovy.transform.AutoClone
import groovy.transform.AutoCloneStyle
import groovy.transform.CompileStatic
import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.databind.ObjectMapper
import com.github.hashicorp.packer.engine.types.InterpolableObject
import com.github.hashicorp.packer.engine.types.InterpolableString
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.Console
import org.gradle.api.tasks.Internal
import org.gradle.api.tasks.Nested
import org.gradle.api.tasks.Optional
import com.fasterxml.jackson.databind.PropertyNamingStrategy
import com.fasterxml.jackson.module.afterburner.AfterburnerModule

@AutoClone(style = AutoCloneStyle.SIMPLE)
@CompileStatic
// REVIEWED
final class Template extends InterpolableObject {
  // TODO
  String path

  @Console
  String description

  @JsonProperty('min_packer_version')
  @Input
  @Optional
  String minVersion

  @Internal
  Map<String, InterpolableString> variables

  @Console
  List<String> sensitiveVariables

  @Nested
  /* TODO: Map<String, Builder> */ List<Builder> builders

  @Nested
  List<Provisioner> provisioners

  @JsonProperty('post-processors')
  @Nested
  List<PostProcessor.PostProcessorArrayDefinition> postProcessors

  // TODO
  Map<String, String> comments

  // TODO
  byte[] rawContents

  private Context envContext

  /**
   * Context used to interpolate variables.
   * Have {@code env} function
   * @return Context used to interpolate variables
   */
  @JsonIgnore
  @Internal
  Context getEnvContext() {
    this.envContext
  }

  private Context variablesContext

  /**
   * Context used to interpolate template itself.
   * Doesn't have {@code env} function, but have variables
   * @return Context used to interpolate template itself
   */
  @JsonIgnore
  @Internal
  Context getVariablesContext() {
    this.variablesContext
  }

  @Override
  protected void doInterpolate() {
    super.doInterpolate() // TOTEST

    // Stage 1
    envContext = context.forVariables
    variables.each.interpolate envContext

    // Stage 2
    variablesContext = context.forTemplateBody(variables)
    for (Builder builder in builders) {
      builder.header.interpolate variablesContext
    }
  }

  final Template interpolateForBuilder(String buildName) {
    if (context.buildName) {
      // This will never be true
      throw new ObjectAlreadyInterpolatedForBuilder()
    }
    interpolate context
    Template result = new Template()
    Builder builder = builders.find { Builder builder -> builder.header.buildName == buildName }
    if (!builder) {
      throw new IllegalArgumentException(sprintf('Build with name `%s` not found.', [buildName]))
    }
    // Stage 3
    builder = builder.clone()
    builder.interpolate context
    result.builders = [builder]
    Context buildCtx = variablesContext.withTemplateVariables([
      (BUILD_NAME_VARIABLE_NAME): buildName,
      'BuilderType': builder.header.type,
    ])

    result.provisioners = provisioners*.interpolateForBuilder(buildCtx).findAll()
    result.postProcessors = postProcessors*.interpolateForBuilder(buildCtx).findAll()
    // Stage 4
    result.run()
    result
  }

  private void run() {
    if (builders.size() != 1) {
      throw new IllegalStateException(sprintf('Expected 1 builder. Found: %d', builders.size()))
    }
    // Stage 4
    Tuple2<Artifact, List<Provider<Boolean>>> builderResult = builders[0].run()
    Boolean keep = true
    upToDateWhen.addAll builderResult.second

    // Provisioners don't add anything to artifacts or upToDateWhen

    postProcessors.each { PostProcessor.PostProcessorArrayDefinition postProcessorArrayDefinition ->
      Tuple2<Tuple2<List<Artifact>, Boolean>, List<Provider<Boolean>>> postProcessorResult = postProcessorArrayDefinition.postProcess(builderResult.first)
      artifacts.addAll postProcessorResult.first.first
      keep = keep || postProcessorResult.first.second
      upToDateWhen.addAll postProcessorResult.second
    }
    if (keep) {
      artifacts.add builderResult.first
    }
  }

  @Inject
  private final ProjectLayout projectLayout

  @JsonIgnore
  @Nested
  final List<Artifact> artifacts = []

  /*ConfigurableFileCollection getArtifacts() {
    projectLayout.configurableFiles(this.artifacts) // TODO
  }*/

  @JsonIgnore
  @Internal
  final List<Provider<Boolean>> upToDateWhen = []

  // @Inject // TOTEST
  // Template(/*ProjectLayout projectLayout*/) {
    // artifacts = projectLayout.configurableFiles()
  // }

  // @PackageScope
  static final ObjectMapper MAPPER = new ObjectMapper()
  static {
    MAPPER.registerModule(new AfterburnerModule())
    MAPPER.propertyNamingStrategy = PropertyNamingStrategy.SNAKE_CASE
    MAPPER.serializationInclusion = JsonInclude.Include.NON_NULL
  }

  static Template readFromFile(File file) {
    (Template)file.withInputStream { InputStream inputStream ->
      MAPPER.readValue(inputStream, Template)
    }
  }
}
