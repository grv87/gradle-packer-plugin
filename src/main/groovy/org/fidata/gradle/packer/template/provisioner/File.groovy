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

import groovy.transform.CompileStatic
import org.fidata.gradle.packer.template.Context
import org.fidata.gradle.packer.template.Provisioner
import org.fidata.gradle.packer.template.enums.Direction
import org.fidata.gradle.packer.template.types.InterpolableBoolean
import org.fidata.gradle.packer.template.types.InterpolableDirection
import org.fidata.gradle.packer.template.types.InterpolableFile
import org.fidata.gradle.packer.template.types.InterpolableString
import org.fidata.gradle.packer.template.types.InterpolableStringArray
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.InputDirectory
import org.gradle.api.tasks.InputFile
import org.gradle.api.tasks.Internal
import org.gradle.api.tasks.Optional
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.OutputFile

@CompileStatic
class File extends Provisioner {
  @Internal
  InterpolableString source

  @Internal
  InterpolableStringArray sources

  @Internal
  InterpolableString destination

  @Input
  InterpolableDirection direction

  @Internal
  InterpolableBoolean generated // TODO

  @Optional
  @InputFile
  RegularFileCollection
  InterpolableFile sourceFile

  @Optional
  @InputDirectory
  InterpolableFile sourceDirectory

  @Optional
  @OutputFile
  InterpolableFile destinationFile

  @Optional
  @OutputDirectory
  InterpolableFile destinationDirectory

  @Override
  protected void doInterpolate(Context ctx) {
    direction.interpolate(ctx)
    switch (direction.interpolatedValue) {
      case Direction.UPLOAD:
        String sourceFileName
        if (source) {
          source.interpolate(ctx)
          if (source.interpolatedValue.endsWith('/') || source.interpolatedValue.endsWith('\\')) {

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
