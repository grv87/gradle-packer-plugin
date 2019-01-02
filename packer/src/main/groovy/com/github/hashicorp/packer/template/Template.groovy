/*
 * Template class
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
 *
 * Ported from original Packer code,
 * file template/template.go
 * under the terms of the Mozilla Public License, v. 2.0.
 */
package com.github.hashicorp.packer.template

import com.fasterxml.jackson.annotation.JsonIgnore

import static Context.BUILD_NAME_VARIABLE_NAME
import org.fidata.packer.engine.BuilderResult
import org.fidata.packer.engine.PostProcessArrayResult
import org.fidata.packer.engine.TemplateBuildResult
import org.fidata.packer.engine.annotations.ExtraProcessed
import com.fasterxml.jackson.annotation.JsonAnyGetter
import com.fasterxml.jackson.annotation.JsonAnySetter
import com.fasterxml.jackson.databind.exc.UnrecognizedPropertyException
import org.fidata.packer.engine.annotations.AutoImplement
import org.fidata.packer.engine.types.InterpolableString
import java.util.regex.Matcher
import java.util.regex.Pattern
import org.fidata.packer.engine.AbstractEngine
import org.fidata.packer.engine.annotations.ComputedInternal
import org.gradle.api.Project
import java.nio.file.Path
import org.fidata.packer.engine.exceptions.ObjectAlreadyInterpolatedForBuilderException
import com.github.hashicorp.packer.packer.Artifact
import java.util.function.Supplier
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

  // Note that Packer doesn't load and store comments
  @JsonIgnore
  @Internal
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

  final Template interpolateForBuilder(AbstractEngine engine, String buildName, Project project) {
    if (context.buildName) {
      // This will never be true
      throw new ObjectAlreadyInterpolatedForBuilderException()
    }
    Builder builder = builders.find { Builder builder -> builder.header.buildName == buildName }
    if (!builder) {
      throw new IllegalArgumentException(sprintf('Build with name `%s` not found.', [buildName]))
    }
    // Stage 3
    Context projectContext = variablesCtx.forProject(project)
    builder = builder.interpolate(projectContext)
    Context buildCtx = projectContext.withTemplateVariables([
      (BUILD_NAME_VARIABLE_NAME): buildName,
      'BuilderType': builder.header.type,
    ])

    Template template = new Interpolated(
      path,
      description,
      minVersion,
      variables,
      sensitiveVariables,
      [builder],
      provisioners*.interpolateForBuilder(engine, buildCtx).findAll(),
      postProcessors*.interpolateForBuilder(engine, buildCtx).findAll()
    )
    template.@comments = comments // TODO

    template
  }

  /**
   * Emulates running the actual build.
   */
  TemplateBuildResult build() {
    // Stage 4
    final List<Artifact> artifacts = []

    final List<Supplier<Boolean>> upToDateWhen = []

    if (builders.size() != 1) {
      throw new IllegalStateException(sprintf('Expected 1 builder. Found: %d', builders.size()))
    }
    // Stage 4
    BuilderResult builderResult = builders[0].run()
    Artifact builderArtifact = builderResult.artifact
    if (builderArtifact == null) {
      // If there was no result, don't worry about running post-processors
      // because there is nothing they can do, just return.
      // TODO: log warn
      return
    }
    boolean keep = true
    upToDateWhen.addAll builderResult.upToDateWhen

    // Provisioners don't add anything to artifacts or upToDateWhen

    postProcessors.each { PostProcessor.PostProcessorArrayDefinition postProcessorArrayDefinition ->
      PostProcessArrayResult postProcessorArrayResult = postProcessorArrayDefinition.postProcess(builderResult.artifact)
      artifacts.addAll postProcessorArrayResult.artifacts
      keep = keep || postProcessorArrayResult.keep
      upToDateWhen.addAll postProcessorArrayResult.upToDateWhen
    }
    if (keep) {
      artifacts.add 0, builderResult.artifact
    }

    new TemplateBuildResult(artifacts, upToDateWhen)
  }

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

  static final class Interpolated extends Template {
    Interpolated(
      Path path,
      String description,
      String minVersion,
      Map<String, InterpolableString> variables,
      List<String> sensitiveVariables,
      List<Builder> builders,
      List<Provisioner> provisioners,
      List<PostProcessor.PostProcessorArrayDefinition> postProcessors
    ) {
    }
  }
}
