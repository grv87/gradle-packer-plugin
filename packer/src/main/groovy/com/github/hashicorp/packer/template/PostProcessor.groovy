/*
 * PostProcessor class
 * Copyright ©  Basil Peace
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

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonTypeInfo
import com.fasterxml.jackson.annotation.JsonUnwrapped
import com.fasterxml.jackson.annotation.JsonValue
import groovy.transform.InheritConstructors
import org.fidata.packer.engine.PostProcessArrayResult
import org.fidata.packer.engine.PostProcessResult
import org.fidata.packer.engine.annotations.AutoImplement
import org.fidata.packer.engine.annotations.Default
import org.fidata.packer.engine.annotations.ExtraProcessed
import org.fidata.packer.engine.exceptions.InvalidRawValueClassException
import org.fidata.packer.engine.Mutability
import org.fidata.packer.engine.AbstractEngine
import com.github.hashicorp.packer.packer.Artifact
import groovy.transform.CompileStatic
import groovy.transform.CompileDynamic
import org.fidata.packer.engine.types.base.InterpolableObject
import org.fidata.packer.engine.types.InterpolableBoolean
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.Nested
import java.util.function.Supplier

/**
 * A PostProcessor is responsible for taking an artifact of a build
 * and doing some sort of post-processing to turn this into another
 * artifact. An example of a post-processor would be something that takes
 * the result of a build, compresses it, and returns a new artifact containing
 * a single file of the prior artifact compressed.
 *
 * @param <ThisClass> Actual implementation class
 */
@JsonTypeInfo(
  use = JsonTypeInfo.Id.NAME,
  include = JsonTypeInfo.As.PROPERTY,
  property = 'type'
)
@AutoImplement
@CompileStatic
abstract class PostProcessor<ThisClass extends PostProcessor<ThisClass>> implements InterpolableObject<ThisClass> {
  @JsonUnwrapped
  @ExtraProcessed
  // TODO
  abstract OnlyExcept getOnlyExcept()

  @Input
  abstract String getType()

  @ExtraProcessed
  @Default({ Boolean.FALSE })
  abstract InterpolableBoolean getKeepInputArtifacts()

  final ThisClass interpolateForBuilder(AbstractEngine engine, Context buildCtx) {
    // Stage 3
    if (onlyExcept?.skip(buildCtx.buildName) != Boolean.TRUE) {
      interpolate buildCtx
    } else {
      null
    }
  }

  final PostProcessResult postProcess(Artifact priorArtifact) {
    // Stage 4
    PostProcessResult result = doPostProcess(priorArtifact)
    new PostProcessResult(result.artifact, result.keep || keepInputArtifacts.interpolated, result.upToDateWhen, result.interactive)
  }

  /**
   * doPostProcess takes a previously created Artifact and produces another
   * Artifact. If an error occurs, it should return that error. If `keep`
   * is to true, then the previous artifact is forcibly kept.
   *
   * Implementations should emulate in this method the run of post-processor
   * and returning information required to configure Gradle task.
   *
   * @param priorArtifact Artifact produced by builder or previous post-processor. Never null
   * @return post-process result
   */
  protected abstract PostProcessResult doPostProcess(Artifact priorArtifact)

  /**
   * Registers this class in specified Engine
   *
   * @param engine Engine to register in
   */
  static void register(AbstractEngine engine) {
    engine.addSubtypeRegistry PostProcessor
  }

  static final class PostProcessorArrayDefinition implements InterpolableObject<PostProcessorArrayDefinition> {
    @InheritConstructors
    static final class ArrayClass extends ArrayList<PostProcessorDefinition> { }

    @JsonValue
    @Nested
    Object rawValue // TODO

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
     * overloaded version of doInterpolatePrimitive
     * depending on rawValue actual type
     */
    @CompileDynamic
    PostProcessorArrayDefinition interpolate(Context context) {
      doInterpolatePrimitive(context, rawValue)
    }

    private static PostProcessorArrayDefinition doInterpolatePrimitive(Context context, ArrayClass rawValue) {
      new PostProcessorArrayDefinition(new ArrayClass(rawValue.collect { PostProcessorDefinition postProcessorDefinition ->
        postProcessorDefinition.interpolate context
      }))
    }

    private static PostProcessorArrayDefinition doInterpolatePrimitive(Context context, PostProcessorDefinition rawValue) {
      new PostProcessorArrayDefinition(rawValue.interpolate(context))
    }

    private static PostProcessorArrayDefinition doInterpolatePrimitive(Context context, Object rawValue) {
      throw new InvalidRawValueClassException(rawValue)
    }

    /*
     * CAVEAT:
     * We use dynamic compiling to run
     * overloaded version of interpolatePrimitiveForBuilder
     * depending on rawValue actual type
     */
    @CompileDynamic
    final PostProcessorArrayDefinition interpolateForBuilder(AbstractEngine engine, Context buildCtx) {
      // Stage 3
      interpolatePrimitiveForBuilder(engine, buildCtx, rawValue)
    }

    private static PostProcessorArrayDefinition interpolatePrimitiveForBuilder(AbstractEngine engine, Context buildCtx, ArrayClass rawValue) {
      ArrayClass result = (ArrayClass)(rawValue*.interpolateForBuilder(engine, buildCtx).findAll())
      if (result.empty) {
        null
      } else {
        new PostProcessorArrayDefinition(result)
      }
    }

    private static PostProcessorArrayDefinition interpolatePrimitiveForBuilder(AbstractEngine engine, Context buildCtx, PostProcessorDefinition rawValue) {
      PostProcessorDefinition result = rawValue.interpolateForBuilder(engine, buildCtx)
      if (result) {
        new PostProcessorArrayDefinition(result)
      } else {
        null
      }
    }

    private static PostProcessorArrayDefinition interpolatePrimitiveForBuilder(AbstractEngine engine, Context buildCtx, Object rawValue) {
      throw new InvalidRawValueClassException(rawValue)
    }

    /*
     * CAVEAT:
     * We use dynamic compiling to run
     * overloaded version of doPostProcess
     * depending on rawValue actual type
     */
    @CompileDynamic
    final PostProcessArrayResult/*TODO: Groovy 2.5.0*/ postProcess(Artifact priorArtifact) {
      // Stage 4
      doPostProcess priorArtifact, rawValue
    }

    private static PostProcessArrayResult doPostProcess(Artifact priorArtifact, ArrayClass rawValue) {
      List<Artifact> artifacts = []
      boolean keep = true
      List<Supplier<Boolean>> upToDateWhen = []
      boolean interactive = false
      Artifact prevArtifact = priorArtifact
      rawValue.eachWithIndex { PostProcessorDefinition postProcessorDefinition, Integer i ->
        PostProcessResult result = postProcessorDefinition.postProcess(prevArtifact)
        Artifact newArtifact = result.artifact
        boolean newKeep = result.keep
        if (newArtifact.is(prevArtifact)) {
          if (!newKeep) {
            artifacts = []
          }
        } else {
          if (newKeep) {
            artifacts.add newArtifact
          } else {
            artifacts = [newArtifact]
          }
          prevArtifact = newArtifact
        }
        keep = keep && newKeep
        upToDateWhen.addAll result.upToDateWhen
        interactive = interactive || result.interactive
      }
      new PostProcessArrayResult(artifacts, keep, upToDateWhen, interactive)
    }

    private static PostProcessArrayResult doPostProcess(Artifact priorArtifact, PostProcessorDefinition rawValue) {
      new PostProcessArrayResult(rawValue.postProcess(priorArtifact))
    }

    private static PostProcessArrayResult doPostProcess(Artifact priorArtifact, Object rawValue) {
      throw new InvalidRawValueClassException(rawValue)
    }
  }

  static final class PostProcessorDefinition implements InterpolableObject<PostProcessorDefinition> {
    @JsonValue
    @Nested
    Object rawValue

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
     * overloaded version of doInterpolatePrimitive
     * depending on rawValue actual type
     */
    @CompileDynamic
    PostProcessorDefinition interpolate(Context context) {
      doInterpolatePrimitive(context, rawValue)
    }

    private static PostProcessorDefinition doInterpolatePrimitive(Context context, PostProcessor rawValue) {
      new PostProcessorDefinition(rawValue.interpolate(context)) // TODO MARK1
    }

    private static PostProcessorDefinition doInterpolatePrimitive(Context context, Object rawValue) {
      throw new InvalidRawValueClassException(rawValue)
    }

    /*
     * CAVEAT:
     * We use dynamic compiling to run
     * overloaded version of interpolatePrimitiveForBuilder
     * depending on rawValue actual type
     */
    @CompileDynamic
    final PostProcessorDefinition interpolateForBuilder(AbstractEngine engine, Context buildCtx) {
      // Stage 3
      interpolatePrimitiveForBuilder(engine, buildCtx, rawValue)
    }

    private static PostProcessorDefinition interpolatePrimitiveForBuilder(AbstractEngine engine, Context buildCtx, PostProcessor rawValue) {
      PostProcessor result = rawValue.interpolateForBuilder(engine, buildCtx)
      if (result) {
        new PostProcessorDefinition(result)
      } else {
        null
      }
    }

    private static PostProcessorDefinition interpolatePrimitiveForBuilder(AbstractEngine engine, Context buildCtx, String rawValue) {
      new PostProcessorDefinition(engine.instantiate(PostProcessor, rawValue, Mutability.IMMUTABLE)) // TODO
    }

    private static PostProcessorDefinition interpolatePrimitiveForBuilder(AbstractEngine engine, Context buildCtx, Object rawValue) {
      throw new InvalidRawValueClassException(rawValue)
    }

    /*
     * CAVEAT:
     * We use dynamic compiling to run
     * overloaded version of doPostProcess
     * depending on rawValue actual type
     */
    @CompileDynamic
    final PostProcessResult postProcess(Artifact priorArtifact) {
      // Stage 4
      doPostProcess priorArtifact, rawValue
    }

    private static PostProcessResult doPostProcess(Artifact priorArtifact, PostProcessor rawValue) {
      rawValue.postProcess priorArtifact
    }

    private static PostProcessResult doPostProcess(Artifact priorArtifact, Object rawValue) {
      throw new InvalidRawValueClassException(rawValue)
    }
  }
}
