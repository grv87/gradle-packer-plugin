/*
 * File class
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
package org.fidata.gradle.packer.template.provisioner

import groovy.transform.AutoClone
import groovy.transform.AutoCloneStyle
import groovy.transform.CompileStatic
import org.fidata.gradle.packer.template.Provisioner
import org.fidata.gradle.packer.template.enums.Direction
import org.fidata.gradle.packer.template.types.InterpolableBoolean
import org.fidata.gradle.packer.template.types.InterpolableDirection
import org.fidata.gradle.packer.template.types.InterpolableFile
import org.fidata.gradle.packer.template.types.InterpolableString
import com.fasterxml.jackson.annotation.JsonIgnore
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.InputDirectory
import org.gradle.api.tasks.InputFile
import org.gradle.api.tasks.Internal
import org.gradle.api.tasks.Optional
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.OutputFile

@AutoClone(style = AutoCloneStyle.SIMPLE)
@CompileStatic
class File extends Provisioner<Configuration> {
  static class Configuration extends Provisioner.Configuration {
    @Internal
    InterpolableString source

    @Internal
    InterpolableString destination

    @Input
    InterpolableDirection direction

    @Internal
    InterpolableBoolean generated // TODO

    @JsonIgnore
    @InputFile
    @Optional
    // TODO RegularFileCollection
    InterpolableFile sourceFile

    @JsonIgnore
    @InputDirectory
    @Optional
    InterpolableFile sourceDirectory

    @JsonIgnore
    @OutputFile
    @Optional
    InterpolableFile destinationFile

    @JsonIgnore
    @OutputDirectory
    @Optional
    InterpolableFile destinationDirectory

    @Override
    protected void doInterpolate() {
      source?.interpolate context
      destination?.interpolate context
      direction?.interpolate context
      Direction _direction = direction?.interpolatedValue ?: Direction.UPLOAD
      generated?.interpolate context
      switch (_direction) {
        case Direction.UPLOAD:
          String sourceFileName
          if (source) {
            if (source.interpolatedValue.endsWith('/') || source.interpolatedValue.endsWith('\\')) {
              // sourceDirectory = TODO

            }
          }

          break
        case Direction.DOWNLOAD:

          break
        default:
          throw new IllegalArgumentException(sprintf('Direction must be one of: download, upload. Got: %s', [direction.interpolatedValue]))
      }
    }
  }
}
