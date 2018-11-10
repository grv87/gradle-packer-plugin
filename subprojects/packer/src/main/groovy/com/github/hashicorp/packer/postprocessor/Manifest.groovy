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


import com.github.hashicorp.packer.engine.types.InterpolableFile
import com.github.hashicorp.packer.engine.types.InterpolableString
import com.github.hashicorp.packer.template.Context
import groovy.transform.AutoClone
import groovy.transform.AutoCloneStyle
import groovy.transform.CompileStatic
import com.github.hashicorp.packer.template.PostProcessor
import com.fasterxml.jackson.annotation.JsonProperty
import org.gradle.api.file.FileCollection
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
  InterpolableString/*File*/ outputPath

  @Input
  InterpolableBoolean stripPath

  @Override
  protected void doInterpolate() {
    // TODO
    super.doInterpolate()
    outputPath.interpolate context
    stripPath.interpolate context
  }

  @Override
  protected Tuple2<Tuple2<com.github.hashicorp.packer.packer.Artifact, Boolean>, List<Provider<Boolean>>> doPostProcess(com.github.hashicorp.packer.packer.Artifact priorArtifact) {
    new Tuple2(new Tuple2(new Artifact(priorArtifact), true), null)
  }

  final class Artifact implements com.github.hashicorp.packer.packer.Artifact {
    static final String BUILDER_ID = 'packer.post-processor.manifest'
    Artifact(com.github.hashicorp.packer.packer.Artifact priorArtifact) {
      com_github_hashicorp_packer_packer_Artifact__builderId = BUILDER_ID
      com_github_hashicorp_packer_packer_Artifact__files = context.resolveFiles(outputPath?.interpolatedValue ?: 'packer-manifest.json' /* TODO*/)
      com_github_hashicorp_packer_packer_Artifact__id = priorArtifact.id
      com_github_hashicorp_packer_packer_Artifact__string = "$context.buildName-$priorArtifact.id"
    }
  }
}
