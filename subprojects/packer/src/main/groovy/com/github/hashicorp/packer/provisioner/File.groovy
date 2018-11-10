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
package com.github.hashicorp.packer.provisioner

import com.github.hashicorp.packer.engine.annotations.Default
import com.github.hashicorp.packer.engine.types.InterpolableEnum
import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonValue
import groovy.transform.AutoClone
import groovy.transform.AutoCloneStyle
import groovy.transform.CompileStatic
import com.github.hashicorp.packer.template.Provisioner
import com.github.hashicorp.packer.engine.types.InterpolableBoolean
import com.github.hashicorp.packer.engine.types.InterpolableString
import com.fasterxml.jackson.annotation.JsonIgnore
import groovy.transform.InheritConstructors
import org.gradle.api.tasks.InputDirectory
import org.gradle.api.tasks.InputFile
import org.gradle.api.tasks.Internal
import org.gradle.api.tasks.Optional
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.OutputFile
import java.nio.file.Path
import java.nio.file.Paths
import java.util.regex.Pattern

@AutoClone(style = AutoCloneStyle.SIMPLE)
@CompileStatic
class File extends Provisioner<Configuration> {
  static class Configuration extends Provisioner.Configuration {
    @Internal
    InterpolableString source

    @Internal
    InterpolableString destination

    @Internal
    // @Default('Direction.UPLOAD')
    InterpolableDirection direction

    /* TODO: Default ?
    @JsonIgnore
    @Input
    Direction getDirectionValue() {
      direction.interpolatedValue ?: Direction.UPLOAD
    }*/

    @Internal
    InterpolableBoolean generated

    private Boolean isDirectory

    private Path inputFile

    @JsonIgnore
    @InputFile
    @Optional
    Path getInputFile() {
      !isDirectory ? this.inputFile : null
    }

    @JsonIgnore
    @InputDirectory
    @Optional
    Path getSourceDirectory() {
      isDirectory ? this.inputFile : null
    }

    private Path outputFile

    @JsonIgnore
    @OutputFile
    @Optional
    Path getOutputFile() {
      !isDirectory ? this.outputFile : null
    }

    @JsonIgnore
    @OutputDirectory
    @Optional
    Path getOutputDirectory() {
      isDirectory ? this.outputFile : null
    }

    private static final Pattern DIR_PATTERN = ~/(\/\\)$/

    @Override
    protected void doInterpolate() {
      super.doInterpolate()
      source?.interpolate context
      destination?.interpolate context
      direction?.interpolate context
      if (!direction?.interpolatedValue) {
        direction = (InterpolableDirection)InterpolableDirection.forInterpolatedValue(Direction.UPLOAD)
      }
      generated?.interpolate context
      if (!generated?.interpolatedValue) {
        generated = (InterpolableBoolean)InterpolableBoolean.forInterpolatedValue(false)
      }

      Path sourcePath = null
      if (source) {
        sourcePath = Paths.get(source.interpolatedValue)
        isDirectory = source.interpolatedValue ==~ DIR_PATTERN
      }

      switch (direction.interpolatedValue /*directionValue*/) {
        case Direction.UPLOAD:
          if (source && !generated.interpolatedValue) {
            inputFile = context.resolvePath(sourcePath)
          }
          break
        case Direction.DOWNLOAD:
          if (source && destination) {
            Path outputPath = Paths.get(destination.interpolatedValue)
            Boolean destinationIsDirectory = destination.interpolatedValue ==~ DIR_PATTERN
            if (destinationIsDirectory) {
              outputPath = outputPath.resolve(sourcePath.getName(sourcePath.nameCount - 1))
            }
            outputFile = context.resolvePath(outputPath)
          }
          break
        default:
          throw new IllegalArgumentException(sprintf('Direction must be one of: download, upload. Got: %s', [direction.interpolatedValue]))
      }
    }
  }

  enum Direction {
    UPLOAD,
    DOWNLOAD

    @JsonValue
    @Override
    String toString() {
      this.name().toLowerCase()
    }
  }

  @AutoClone(style = AutoCloneStyle.SIMPLE)
  @InheritConstructors
// @KnownImmutable // TODO: Groovy 2.5
  static class InterpolableDirection extends InterpolableEnum<Direction>  {
  }
}
