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
package org.fidata.gradle.packer.template.post_processor

import com.fasterxml.jackson.annotation.JsonIgnore
import groovy.transform.AutoClone
import groovy.transform.AutoCloneStyle
import groovy.transform.CompileStatic
import org.fidata.gradle.packer.template.PostProcessor
import com.fasterxml.jackson.annotation.JsonProperty
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.Internal
import org.gradle.api.tasks.OutputFile
import org.fidata.gradle.packer.template.types.InterpolableString
import org.fidata.gradle.packer.template.types.InterpolableBoolean

@AutoClone(style = AutoCloneStyle.SIMPLE)
@CompileStatic
class Manifest extends PostProcessor {
  @JsonProperty('output')
  @Internal
  InterpolableString outputPath

  @Input
  InterpolableBoolean stripPath

  @JsonIgnore
  @OutputFile
  File getOutputFile() {
    context.task.project.file(outputPath.interpolatedValue ?: 'packer-manifest.json')
  }

  @Override
  protected void doInterpolate() {
    super.doInterpolate()
    // TODO
  }
}
