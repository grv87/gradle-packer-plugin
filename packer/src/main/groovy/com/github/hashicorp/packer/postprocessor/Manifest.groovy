/*
 * Manifest post-processor
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
 * file post-processor/manifest/post-processor.go
 * under the terms of the Mozilla Public License, v. 2.0.
 */
package com.github.hashicorp.packer.postprocessor

import org.fidata.packer.engine.AbstractEngine
import org.fidata.packer.engine.PostProcessResult
import org.fidata.packer.engine.annotations.AutoImplement
import org.fidata.packer.engine.annotations.Default
import org.fidata.packer.engine.annotations.ExtraProcessed
import org.fidata.packer.engine.types.InterpolableString
import groovy.transform.CompileStatic
import com.github.hashicorp.packer.template.PostProcessor
import com.fasterxml.jackson.annotation.JsonProperty
import org.gradle.api.provider.Provider
import org.gradle.api.tasks.Input
import org.fidata.packer.engine.types.InterpolableBoolean

@AutoImplement
@CompileStatic
abstract class Manifest extends PostProcessor<Manifest> {
  @JsonProperty('output')
  @Default({ 'packer-manifest.json' })
  @ExtraProcessed
  abstract InterpolableString/*File*/ getOutputPath()

  @Input
  abstract InterpolableBoolean getStripPath()

  @Override
  protected final PostProcessResult doPostProcess(com.github.hashicorp.packer.packer.Artifact priorArtifact) {
    new PostProcessResult(
      new Artifact(priorArtifact),
      true,
      Collections.EMPTY_LIST
    )
  }

  class Artifact implements com.github.hashicorp.packer.packer.Artifact {
    static final String BUILDER_ID = 'packer.post-processor.manifest'
    Artifact(com.github.hashicorp.packer.packer.Artifact priorArtifact) {
      com_github_hashicorp_packer_packer_Artifact__builderId = BUILDER_ID
      com_github_hashicorp_packer_packer_Artifact__files = context.resolveFiles(outputPath?.interpolated /* TODO*/) // MARK2
      com_github_hashicorp_packer_packer_Artifact__id = priorArtifact.id
      com_github_hashicorp_packer_packer_Artifact__string = "$context.buildName-$priorArtifact.id"
    }
  }

  static void register(AbstractEngine engine) {
    engine.registerSubtype PostProcessor, 'manifest', this
  }
}
