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

import java.nio.file.Path
import java.util.regex.Matcher
import java.util.regex.Pattern

@AutoClone(style = AutoCloneStyle.SIMPLE, excludes = ['sourceFile', 'sourceDirectory', 'destinationFile', 'destinationDirectory'])
@CompileStatic
class File extends Provisioner<Configuration> {
  static class Configuration extends Provisioner.Configuration {
    @Internal
    InterpolableString source

    @Internal
    InterpolableString destination

    @Internal
    InterpolableDirection direction

    @JsonIgnore
    @Input
    Direction getDirectionValue() {
      direction.interpolatedValue ?: Direction.UPLOAD
    }

    @Internal
    InterpolableBoolean generated // TODO

    @JsonIgnore
    @InputFile
    @Optional
    // TODO RegularFileCollection
    java.io.File sourceFile

    @JsonIgnore
    @InputDirectory
    @Optional
    java.io.File sourceDirectory

    @JsonIgnore
    @OutputFile
    @Optional
    java.io.File destinationFile

    @JsonIgnore
    @OutputDirectory
    @Optional
    java.io.File destinationDirectory

    private static final Pattern DIR_PATTERN = ~/(\/\\)$/

    @Override
    protected void doInterpolate() {
      super.doInterpolate()
      source?.interpolate context
      destination?.interpolate context
      direction?.interpolate context
      generated?.interpolate context

      Path sourceFileName = null
      Boolean sourceIsDirectory
      if (source) {
        sourceFileName = new java.io.File(source.interpolatedValue).toPath()
        sourceIsDirectory = source.interpolatedValue ==~ DIR_PATTERN
      }

      switch (directionValue) {
        case Direction.UPLOAD:
          if (source && !generated?.interpolatedValue) {
            if (sourceIsDirectory) {
              sourceDirectory = context.task.project.file(sourceFileName)
            } else {
              sourceFile = context.task.project.file(sourceFileName)
            }
          }
          break
        case Direction.DOWNLOAD:
          if (source && destination) {
            Path destinationFileName = new java.io.File(destination.interpolatedValue).toPath()
            Boolean destinationIsDirectory = destination.interpolatedValue ==~ DIR_PATTERN
            if (destinationIsDirectory) {
              destinationFileName = destinationFileName.resolve(sourceFileName.getName(sourceFileName.nameCount - 1))
            }
            if (sourceIsDirectory) {
              destinationDirectory = context.task.project.file(destinationFileName)
            } else {
              destinationFile = context.task.project.file(destinationFileName)
            }
          }
          break
        default:
          throw new IllegalArgumentException(sprintf('Direction must be one of: download, upload. Got: %s', [direction.interpolatedValue]))
      }
    }
  }
}
