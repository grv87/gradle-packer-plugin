/*
 * FloppyConfig class
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
 * file common/floppy_config.go
 * under the terms of the Mozilla Public License, v. 2.0.
 */
package com.github.hashicorp.packer.common

import org.fidata.gradle.utils.InputDirectoryFlatWrapper
import org.fidata.gradle.utils.InputDirectoryWrapper
import org.fidata.packer.engine.annotations.AutoImplement
import org.fidata.packer.engine.annotations.ComputedInputFiles
import org.fidata.packer.engine.annotations.ComputedNested
import org.fidata.packer.engine.annotations.ExtraProcessed
import org.fidata.packer.engine.types.base.InterpolableObject
import org.fidata.packer.engine.types.InterpolableStringArray
import groovy.transform.CompileStatic

@AutoImplement
@CompileStatic
abstract class FloppyConfig implements InterpolableObject<FloppyConfig> {
  @ExtraProcessed
  abstract InterpolableStringArray getFloppyFiles()

  @ExtraProcessed
  abstract InterpolableStringArray getFloppyDirectories()

  // MARK2
  @ComputedNested
  List<InputDirectoryWrapper> getInputDirectories() {
    // TOTHINK: cache result ?

  }

  @ComputedNested
  List<InputDirectoryFlatWrapper> getInputDirectoriesFlat() {
    // TOTHINK: cache result ?

  }

  @ComputedInputFiles
  List<File> getInputFiles() {
    // TOTHINK: cache result ?

  }

  // Wildcard characters (*, ?, and []) are allowed. Directory names are also allowed, which will add all the files found in the directory to the floppy
}
