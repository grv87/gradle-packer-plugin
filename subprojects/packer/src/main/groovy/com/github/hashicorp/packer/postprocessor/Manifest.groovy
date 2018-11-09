/*
 * Manifest class
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
package com.github.hashicorp.packer.postprocessor

import com.github.hashicorp.packer.engine.types.InterpolablePath
import groovy.transform.AutoClone
import groovy.transform.AutoCloneStyle
import groovy.transform.CompileStatic
import com.github.hashicorp.packer.template.PostProcessor
import com.fasterxml.jackson.annotation.JsonProperty
import org.gradle.api.provider.Provider
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.Internal
import com.github.hashicorp.packer.engine.types.InterpolableBoolean
import java.nio.file.Paths

@AutoClone(style = AutoCloneStyle.SIMPLE)
@CompileStatic
class Manifest extends PostProcessor {
  @JsonProperty('output')
  @Internal
  // TODO: @Default('packer-manifest.json')
  InterpolablePath outputPath

  @Input
  InterpolableBoolean stripPath

  // TODO
  class Artifact implements com.github.hashicorp.packer.packer.Artifact {
    @Override
    String builderId() {
      'packer.post-processor.manifest'
    }
    final String buildName
    final String builderType
    final long buildTime
    final ArtifactFiles
    final String artifactId
    final UUID packerRunUUID

    Artifact(com.github.hashicorp.packer.packer.Artifact priorArtifact) {


      builderId: ,
      files: context.resolveFiles(outputPath.interpolatedValue ?: Paths.get('packer-manifest.json') /* TODO */),
      id: priorArtifact.id,
      string: "$context.buildName-$priorArtifact.id"
    }
  }

  @Override
  protected Tuple2<Tuple2<Artifact, Boolean>, List<Provider<Boolean>>> doPostProcess(Artifact priorArtifact) {
    Artifact artifact = new Artifact() {

    }

      new Artifact() {

    }

    }
      // TODO: Packer creates the whole subclass and instance here. We just use generic class for now
      builderId: 'packer.post-processor.manifest',
      files: context.resolveFiles(outputPath.interpolatedValue ?: Paths.get('packer-manifest.json') /* TODO */),
      id: priorArtifact.id,
      string: "$context.buildName-$priorArtifact.id",
      // TODO
    )
    return new Tuple2(new Tuple2(artifact, true), null)
  }

  /*@Override
  protected void doInterpolate() {
    super.doInterpolate()
    // TODO
  }*/
}
