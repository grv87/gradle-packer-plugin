/*
 * PostProcessor class
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


import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonTypeInfo
import com.fasterxml.jackson.annotation.JsonUnwrapped
import com.fasterxml.jackson.annotation.JsonValue
import org.fidata.packer.engine.exceptions.InvalidRawValueClassException
import org.fidata.packer.engine.exceptions.ObjectAlreadyInterpolatedForBuilderException
import org.fidata.packer.engine.Mutability
import org.fidata.packer.engine.Engine
import org.fidata.packer.engine.SubtypeRegistry
import com.github.hashicorp.packer.packer.Artifact
import groovy.transform.CompileStatic
import groovy.transform.CompileDynamic
import org.fidata.packer.engine.types.base.InterpolableObject
import org.fidata.packer.engine.types.InterpolableBoolean
import org.gradle.api.provider.Provider
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.Internal
import org.gradle.api.tasks.Nested

@JsonTypeInfo(
  use = JsonTypeInfo.Id.NAME,
  include = JsonTypeInfo.As.PROPERTY,
  property = 'type'
)
@CompileStatic
// REVIEWED
abstract class PostProcessor extends InterpolableObject {
  protected PostProcessor() {
  }

  @JsonUnwrapped
  @Internal
  // TODO
  OnlyExcept onlyExcept

  @Input
  String type

  @Internal
  InterpolableBoolean keepInputArtifacts = InterpolableBoolean.withDefault(false)

  @Override
  protected void doInterpolate() {
    keepInputArtifacts.interpolate context
  }

  final PostProcessor interpolateForBuilder(Context buildCtx) {
    if (context.buildName) {
      throw new ObjectAlreadyInterpolatedForBuilderException()
    }
    // Stage 3
    if (/*onlyExcept == null ||*/ !onlyExcept?.skip(buildCtx.buildName)) {
      PostProcessor result = this.clone() // TODO
      result.interpolate buildCtx
      result
    } else {
      null
    }
  }

  final Tuple3<Artifact, Boolean, List<Provider<Boolean>>> postProcess(Artifact priorArtifact) {
    if (!interpolated) {
      throw new IllegalStateException('') // TODO
    }
    // Stage 4
    Tuple3<Artifact, Boolean, List<Provider<Boolean>>> result = doPostProcess(priorArtifact)
    new Tuple3(result.first, result.second || keepInputArtifacts.interpolatedValue, result.third)
  }

  protected abstract Tuple3<Artifact, Boolean, List<Provider<Boolean>>> doPostProcess(Artifact priorArtifact)

  protected static final SubtypeRegistry<PostProcessor> SUBTYPE_REGISTRY = new SubtypeRegistry<PostProcessor>()

  static final class PostProcessorArrayDefinition extends InterpolableObject {
    static class ArrayClass extends ArrayList<PostProcessorDefinition> {
    }

    @JsonValue
    @Nested
    Object rawValue

    private PostProcessorArrayDefinition() {
    }

    @JsonCreator(mode = JsonCreator.Mode.DELEGATING)
    PostProcessorArrayDefinition(ArrayClass rawValue) {
      this.rawValue = rawValue
    }

    @JsonCreator(mode = JsonCreator.Mode.DELEGATING)
    PostProcessorArrayDefinition(PostProcessorDefinition rawValue) {
      this.rawValue = rawValue
    }

    @Override
    /*
     * CAVEAT:
     * We use dynamic compiling to run
     * overloaded version of interpolateRawValue
     * depending on rawValue actual type
     */
    @CompileDynamic
    protected void doInterpolate() {
      interpolateRawValue rawValue
    }

    private void interpolateRawValue(ArrayClass rawValue) {
      rawValue.each { PostProcessorDefinition postProcessorDefinition ->
        postProcessorDefinition.interpolate context
      }
    }

    private void interpolateRawValue(PostProcessorDefinition rawValue) {
      rawValue.interpolate context
    }

    private void interpolateRawValue(Object rawValue) {
      throw new InvalidRawValueClassException(rawValue)
    }

    /*
     * CAVEAT:
     * We use dynamic compiling to run
     * overloaded version of interpolateRawValueForBuilder
     * depending on rawValue actual type
     */
    @CompileDynamic
    final PostProcessorArrayDefinition interpolateForBuilder(Context buildCtx) {
      if (context.buildName) {
        throw new ObjectAlreadyInterpolatedForBuilderException()
      }
      // Stage 3
      interpolateRawValueForBuilder buildCtx, rawValue
    }

    private static PostProcessorArrayDefinition interpolateRawValueForBuilder(Context buildCtx, ArrayClass rawValue) {
      ArrayClass result = (ArrayClass)(rawValue*.interpolateForBuilder(buildCtx).findAll())
      if (result.empty == false) {
        new PostProcessorArrayDefinition(result)
      } else {
        null
      }
    }

    private static PostProcessorArrayDefinition interpolateRawValueForBuilder(Context buildCtx, PostProcessorDefinition rawValue) {
      PostProcessorDefinition result = rawValue.interpolateForBuilder(buildCtx)
      if (result) {
        new PostProcessorArrayDefinition(result)
      } else {
        null
      }
    }

    private static PostProcessorArrayDefinition interpolateRawValueForBuilder(Context buildCtx, Object rawValue) {
      throw new InvalidRawValueClassException(rawValue)
    }

    /*
     * CAVEAT:
     * We use dynamic compiling to run
     * overloaded version of doPostProcess
     * depending on rawValue actual type
     */
    @CompileDynamic
    final Tuple3<List<Artifact>, Boolean, List<Provider<Boolean>>>/*TODO: Groovy 2.5.0*/ postProcess(Artifact priorArtifact) {
      if (!interpolated) {
        throw new IllegalStateException('') // TODO
      }
      // Stage 4
      doPostProcess priorArtifact, rawValue
    }

    private Tuple3<List<Artifact>, Boolean, List<Provider<Boolean>>> doPostProcess(Artifact priorArtifact, ArrayClass rawValue) {
      List<Artifact> artifacts = []
      Boolean keep = true
      List<Provider<Boolean>> upToDateWhen = []
      Artifact _priorArtifact = priorArtifact
      rawValue.eachWithIndex { PostProcessorDefinition postProcessorDefinition, Integer i ->
        Tuple3<Artifact, Boolean, List<Provider<Boolean>>> result = postProcessorDefinition.postProcess(_priorArtifact)
        _priorArtifact = result.first
        boolean _keep = result.second
        keep = keep && _keep
        if (_keep) {
          artifacts.add _priorArtifact
        } else {
          artifacts = [_priorArtifact]
        }
        upToDateWhen.addAll result.third
      }
      new Tuple3(artifacts, keep, upToDateWhen)
    }

    private Tuple3<List<Artifact>, Boolean, List<Provider<Boolean>>> doPostProcess(Artifact priorArtifact, PostProcessorDefinition rawValue) {
      Tuple3<Artifact, Boolean, List<Provider<Boolean>>> result = rawValue.postProcess(priorArtifact)
      new Tuple3([result.first], result.second, result.third)
    }

    private Tuple3<List<Artifact>, Boolean, List<Provider<Boolean>>> doPostProcess(Artifact priorArtifact, Object rawValue) {
      throw new InvalidRawValueClassException(rawValue)
    }
  }

    static final class PostProcessorDefinition extends InterpolableObject {
    @JsonValue
    @Nested
    Object rawValue

    private PostProcessorDefinition() {
    }

    @JsonCreator(mode = JsonCreator.Mode.DELEGATING)
    PostProcessorDefinition(String rawValue) {
      this.rawValue = rawValue
    }

    @JsonCreator(mode = JsonCreator.Mode.DELEGATING)
    PostProcessorDefinition(PostProcessor rawValue) {
      this.rawValue = rawValue
    }

    @Override
    /*
     * CAVEAT:
     * We use dynamic compiling to run
     * overloaded version of interpolateRawValue
     * depending on rawValue actual type
     */
    @CompileDynamic
    protected void doInterpolate() {
      interpolateRawValue rawValue
    }

    private void interpolateRawValue(PostProcessor rawValue) {
      rawValue.interpolate context
    }

    private void interpolateRawValue(String rawValue) {
    }

    private void interpolateRawValue(Object rawValue) {
      throw new InvalidRawValueClassException(rawValue)
    }

    /*
     * CAVEAT:
     * We use dynamic compiling to run
     * overloaded version of interpolateForBuilder
     * depending on rawValue actual type
     */
    @CompileDynamic
    final PostProcessorDefinition interpolateForBuilder(Context buildCtx) {
      if (context.buildName) {
        throw new ObjectAlreadyInterpolatedForBuilderException()
      }
      // Stage 3
      interpolateRawValueForBuilder buildCtx, rawValue
    }

    private static PostProcessorDefinition interpolateRawValueForBuilder(Context buildCtx, PostProcessor rawValue) {
      PostProcessor result = rawValue.interpolateForBuilder(buildCtx)
      if (result) {
        new PostProcessorDefinition(result)
      } else {
        null
      }
    }

    private static PostProcessorDefinition interpolateRawValueForBuilder(Context buildCtx, String rawValue) {
      new PostProcessorDefinition(Engine.ABSTRACT_TYPE_MAPPING_REGISTRY.instantiate(SUBTYPE_REGISTRY[rawValue], Mutability.IMMUTABLE)) // TODO
    }

    private static PostProcessorDefinition interpolateRawValueForBuilder(Context buildCtx, Object rawValue) {
      throw new InvalidRawValueClassException(rawValue)
    }

    /*
     * CAVEAT:
     * We use dynamic compiling to run
     * overloaded version of doPostProcess
     * depending on rawValue actual type
     */
    @CompileDynamic
    final Tuple3<Artifact, Boolean, List<Provider<Boolean>>> postProcess(Artifact priorArtifact) {
      if (!interpolated) {
        throw new IllegalStateException('') // TODO
      }
      // Stage 4
      doPostProcess priorArtifact, rawValue
    }

    private Tuple3<Artifact, Boolean, List<Provider<Boolean>>> doPostProcess(Artifact priorArtifact, PostProcessor rawValue) {
      rawValue.postProcess priorArtifact
    }

    private Tuple3<List<Artifact>, Boolean, List<Provider<Boolean>>> doPostProcess(Artifact priorArtifact, Object rawValue) {
      throw new InvalidRawValueClassException(rawValue)
    }
  }
}
