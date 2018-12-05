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

import com.github.hashicorp.packer.engine.types.InterpolableValue

import static Context.BUILD_NAME_VARIABLE_NAME
import com.fasterxml.jackson.databind.module.SimpleModule
import com.fasterxml.jackson.datatype.guava.GuavaModule
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module
import com.github.hashicorp.packer.engine.annotations.ComputedInternal
import com.github.hashicorp.packer.engine.annotations.ComputedNested
import com.github.hashicorp.packer.engine.types.InterpolableBoolean
import com.github.hashicorp.packer.engine.types.InterpolableLong
import org.gradle.api.Project
import java.nio.file.Path
import groovy.transform.CompileDynamic
import com.github.hashicorp.packer.engine.exceptions.ObjectAlreadyInterpolatedForBuilderException
import com.github.hashicorp.packer.packer.Artifact
import org.gradle.api.provider.Provider
import groovy.transform.CompileStatic
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

@CompileStatic
// REVIEWED
class Template extends InterpolableObject {
  // TODO
  Path path

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

  private Context envCtx

  /**
   * Context used to interpolate variables.
   * Have {@code env} function
   * @return Context used to interpolate variables
   */
  @ComputedInternal
  Context getenvCtx() {
    this.envCtx
  }

  private Context variablesCtx

  /**
   * Context used to interpolate template itself.
   * Doesn't have {@code env} function, but have variables
   * @return Context used to interpolate template itself
   */
  @ComputedInternal
  Context getvariablesCtx() {
    this.variablesCtx
  }

  @Override
  protected void doInterpolate() {
    super.doInterpolate() // TOTEST

    // Stage 1
    envCtx = context.forVariables
    variables.each.interpolate envCtx

    // Stage 2
    variablesCtx = context.forTemplateBody(variables)
    for (Builder builder in builders) {
      builder.header.interpolate variablesCtx
    }
  }

  final Template interpolateForBuilder(String buildName, Project project) {
    if (context.buildName) {
      // This will never be true
      throw new ObjectAlreadyInterpolatedForBuilderException()
    }
    interpolate context
    Template result = new Template()
    Builder builder = builders.find { Builder builder -> builder.header.buildName == buildName }
    if (!builder) {
      throw new IllegalArgumentException(sprintf('Build with name `%s` not found.', [buildName]))
    }
    // Stage 3
    Context projectContext = variablesCtx.forProject(project)
    builder = builder.clone()
    builder.interpolate context
    result.builders = [builder]
    Context buildCtx = projectContext.withTemplateVariables([
      (BUILD_NAME_VARIABLE_NAME): buildName,
      'BuilderType': builder.header.type,
    ])

    result.provisioners = provisioners*.interpolateForBuilder(buildCtx).findAll()
    result.postProcessors = postProcessors*.interpolateForBuilder(buildCtx).findAll()
    // Stage 4
    result.run()
    result
  }

  /*
   * WORKAROUND:
   * Groovy bug https://issues.apache.org/jira/browse/GROOVY-7985.
   * Nested generics are not supported in static compile mode.
   * Fixed in Groovy 2.5.0-rc-3
   * <grv87 2018-11-10>
   */
  @CompileDynamic
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

  @ComputedNested
  final List<Artifact> artifacts = []

  @ComputedInternal
  final List<Provider<Boolean>> upToDateWhen = []

  // @Inject // TOTEST
  // Template(/*ProjectLayout projectLayout*/) {
    // artifacts = projectLayout.configurableFiles()
  // }

  // @PackageScope
  static final ObjectMapper MAPPER = new ObjectMapper()
  static {
    MAPPER.registerModule(new AfterburnerModule())
    MAPPER.registerModule(new GuavaModule())
    MAPPER.registerModule(new Jdk8Module())
    // Annotations replaced this
    // MAPPER.propertyNamingStrategy = PropertyNamingStrategy.SNAKE_CASE
    MAPPER.serializationInclusion = JsonInclude.Include.NON_NULL
    MAPPER.registerModule(InterpolableValue.SERIALIZER_MODULE)
    /*
     * TODO:
     * 1. All classes
     * 2. Mutable and immutable versions
     */
    SimpleModule immutableModule = new SimpleModule()
    immutableModule.addAbstractTypeMapping(InterpolableBoolean, InterpolableBoolean.ImmutableRaw)
    immutableModule.addAbstractTypeMapping(InterpolableString, InterpolableString.ImmutableRaw)
    immutableModule.addAbstractTypeMapping(InterpolableLong, InterpolableLong.ImmutableRaw)
    MAPPER.registerModule(immutableModule);
  }

  static Template readFromFile(File file) {
    Template template = (Template)file.withInputStream { InputStream inputStream ->
      MAPPER.readValue(inputStream, Template)
    }
    template.path = file.toPath()
    template
  }
}
