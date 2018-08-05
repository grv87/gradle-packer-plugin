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
import org.fidata.gradle.packer.template.internal.TemplateObject
import org.fidata.gradle.packer.template.types.Direction
import org.fidata.gradle.packer.template.types.TemplateFile
import org.fidata.gradle.packer.template.types.TemplateString
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.InputDirectory
import org.gradle.api.tasks.InputFile
import org.gradle.api.tasks.Internal
import org.gradle.api.tasks.Nested
import org.gradle.api.tasks.Optional
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.OutputFile

@CompileStatic
class File extends TemplateObject implements Provisioner {
  @Internal
  TemplateString source

  @Internal
  List<TemplateString> sources

  @Internal
  TemplateString destination

  @Internal
  TemplateString direction

  @Internal
  Boolean generated // TODO

  @Input
  Direction direction1

  @Optional
  @InputFile
  RegularFileCollection
  File sourceFile

  @Optional
  @InputDirectory
  File sourceDirectory

  @Optional
  @OutputFile
  File destinationFile

  @Optional
  @OutputDirectory
  File destinationDirectory

  @Override
  protected void doInterpolate(Context ctx) {
    direction.interpolate(ctx)
    direction1 = Direction.forValue(direction.interpolatedValue)
    switch (direction1) {
      case Direction.UPLOAD:
        String sourceFileName
        if (source) {
          if (source.value.endsWith('/') || source.value.endsWith('\\')) {

          }
        }

        break;
      case Direction.DOWNLOAD:

        break;
      default:
        throw new IllegalArgumentException(sprintf('Direction must be one of: download, upload. Got: %s', [direction1]))
    }


  }
}
