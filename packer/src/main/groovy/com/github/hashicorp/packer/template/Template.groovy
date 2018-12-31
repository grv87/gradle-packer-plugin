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

import org.fidata.packer.engine.annotations.ExtraProcessed

import static Context.BUILD_NAME_VARIABLE_NAME
import com.fasterxml.jackson.annotation.JsonAnyGetter
import com.fasterxml.jackson.annotation.JsonAnySetter
import com.fasterxml.jackson.databind.exc.UnrecognizedPropertyException
import org.fidata.packer.engine.annotations.AutoImplement
import org.fidata.packer.engine.types.InterpolableString
import java.util.regex.Matcher
import java.util.regex.Pattern
import org.fidata.packer.engine.AbstractEngine
import org.fidata.packer.engine.annotations.ComputedInternal
import org.fidata.packer.engine.annotations.ComputedNested
import org.gradle.api.Project
import java.nio.file.Path
import org.fidata.packer.engine.exceptions.ObjectAlreadyInterpolatedForBuilderException
import com.github.hashicorp.packer.packer.Artifact
import org.gradle.api.provider.Provider
import groovy.transform.CompileStatic
import com.fasterxml.jackson.annotation.JsonProperty
import org.fidata.packer.engine.types.base.InterpolableObject
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.Console
import org.gradle.api.tasks.Internal
import org.gradle.api.tasks.Nested
import org.gradle.api.tasks.Optional

@CompileStatic
@AutoImplement
abstract class Template implements InterpolableObject<Template> {
  // TODO
  abstract Path getPath()

  @Console
  abstract String getDescription()

  @JsonProperty('min_packer_version')
  @Input
  @Optional
  abstract String getMinVersion()

  @ExtraProcessed
  abstract Map<String, InterpolableString> getVariables()

  @Console
  abstract List<String> getSensitiveVariables()

  @Nested
  abstract /* TODO: Map<String, Builder> */ List<Builder> getBuilders()

  @Nested
  abstract List<Provisioner> getProvisioners()

  @JsonProperty('post-processors')
  @Nested
  abstract List<PostProcessor.PostProcessorArrayDefinition> getPostProcessors()

  // Note that this is
  @ComputedInternal
  abstract Map<String, Object> getComments()

  // TODO
  // byte[] rawContents

  // TODO
  //  private Context envCtx
  //
  //  /**
  //   * Context used to interpolate variables.
  //   * Have {@code env} function
  //   * @return Context used to interpolate variables
  //   */
  //  @ComputedInternal
  //  Context getenvCtx() {
  //    this.envCtx
  //  }
  //
  //  private Context variablesCtx
  //
  //  /**
  //   * Context used to interpolate template itself.
  //   * Doesn't have {@code env} function, but have variables
  //   * @return Context used to interpolate template itself
  //   */
  //  @ComputedInternal
  //  Context getvariablesCtx() {
  //    this.variablesCtx
  //  }

  @Override
  protected Template interpolate() {
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
      Tuple3<List<Artifact>, Boolean, List<Provider<Boolean>>> postProcessorResult = postProcessorArrayDefinition.postProcess(builderResult.first)
      artifacts.addAll postProcessorResult.first
      keep = keep || postProcessorResult.second
      upToDateWhen.addAll postProcessorResult.third
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

  static Template readFromFile(AbstractEngine<Template> engine, File file) { // MARK1
    Template template = file.withInputStream { InputStream inputStream ->
      engine.readValue(inputStream)
    }
    template.path = file.toPath()
    template
  }

  static Template readValue(AbstractEngine<Template> engine, String string) {
    Template template = engine.readValue(string)
    template
  }

  private static final String COMMENT_KEY_PREFIX = '_'
  private static final Pattern COMMENT_KEY_PREFIX_PATTERN = ~/\A$COMMENT_KEY_PREFIX(.+)\z/

  @Internal
  @JsonAnyGetter
  Map<String, Object> getCommentsForJson() {
    comments.collectEntries { String key, Object value ->
      ["$COMMENT_KEY_PREFIX$key": value]
    }
  }

  @JsonAnySetter
  void addCommentFromJson(String name, Object value) {
    Matcher matcher = name =~ COMMENT_KEY_PREFIX_PATTERN
    if (matcher.matches()) {
      comments.put matcher.group(1), value
    } else {
      throw UnrecognizedPropertyException.from(null, this, name, null)
    }
  }
}
