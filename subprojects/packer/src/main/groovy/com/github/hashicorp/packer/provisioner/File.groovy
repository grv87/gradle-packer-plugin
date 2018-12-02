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

import java.io.File as JavaFile
import com.github.hashicorp.packer.engine.annotations.ComputedInputDirectory
import com.github.hashicorp.packer.engine.annotations.ComputedInputFile
import com.github.hashicorp.packer.engine.annotations.ComputedOutputDirectory
import com.github.hashicorp.packer.engine.annotations.ComputedOutputFile
import com.github.hashicorp.packer.engine.types.InterpolableEnum
import com.fasterxml.jackson.annotation.JsonValue
import groovy.transform.CompileStatic
import com.github.hashicorp.packer.template.Provisioner
import com.github.hashicorp.packer.engine.types.InterpolableBoolean
import com.github.hashicorp.packer.engine.types.InterpolableString
import groovy.transform.InheritConstructors
import org.gradle.api.tasks.Internal
import org.gradle.api.tasks.Optional
import java.nio.file.Path
import java.nio.file.Paths
import java.util.regex.Pattern

@CompileStatic
class File extends Provisioner<Configuration> {
  static class Configuration extends Provisioner.Configuration {
    @Internal
    InterpolableString source

    @Internal
    InterpolableString destination

    @Internal
    InterpolableDirection direction = InterpolableDirection.withDefault(Direction.UPLOAD)

    @Internal
    InterpolableBoolean generated = InterpolableBoolean.withDefault(false)

    private Boolean isDirectory

    private JavaFile inputFile

    @ComputedInputFile
    @Optional
    JavaFile getInputFile() {
      !isDirectory ? this.inputFile : null
    }

    @ComputedInputDirectory
    @Optional
    JavaFile getSourceDirectory() {
      isDirectory ? this.inputFile : null
    }

    private JavaFile outputFile

    @ComputedOutputFile
    @Optional
    JavaFile getOutputFile() {
      !isDirectory ? this.outputFile : null
    }

    @ComputedOutputDirectory
    @Optional
    JavaFile getOutputDirectory() {
      isDirectory ? this.outputFile : null
    }

    private static final Pattern DIR_PATTERN = ~/(\/\\)$/

    @Override
    protected void doInterpolate() {
      super.doInterpolate()
      source?.interpolate context
      destination?.interpolate context
      direction?.interpolate context // TODO: null ?
      generated.interpolate context // TODO: null ?

      Path sourcePath = null
      if (source) {
        String sourcePathString = source.get()
        sourcePath = Paths.get(sourcePathString)
        isDirectory = sourcePathString ==~ DIR_PATTERN
      }

      switch (direction.get() /*directionValue*/) {
        case Direction.UPLOAD:
          if (source && !generated.get()) {
            inputFile = context.resolveFile(sourcePath)
          }
          break
        case Direction.DOWNLOAD:
          if (source && destination) {
            Path outputPath = Paths.get(destination.get())
            Boolean destinationIsDirectory = destination.get() ==~ DIR_PATTERN
            if (destinationIsDirectory) {
              outputPath = outputPath.resolve(sourcePath.getName(sourcePath.nameCount - 1))
            }
            outputFile = context.resolveFile(outputPath)
          }
          break
        default:
          throw new IllegalArgumentException(sprintf('Direction must be one of: download, upload. Got: %s', [direction.get()]))
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

  interface InterpolableDirection extends InterpolableEnum<Direction, InterpolableDirection> {
    @InheritConstructors
    final class ImmutableRaw extends InterpolableEnum.ImmutableRaw<Direction, InterpolableDirection> implements InterpolableDirection { }

    @InheritConstructors
    final class Raw extends InterpolableEnum.ImmutableRaw<Direction, InterpolableDirection> implements InterpolableDirection { }

    @InheritConstructors
    final class Interpolated extends InterpolableEnum.ImmutableRaw<Direction, InterpolableDirection> implements InterpolableDirection { }

    @InheritConstructors
    final class AlreadyInterpolated extends InterpolableEnum.ImmutableRaw<Direction, InterpolableDirection> implements InterpolableDirection { }
  }
}
