/*
 * ShellLocal class
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
 */
package com.github.hashicorp.packer.postprocessor

import org.fidata.packer.engine.AbstractEngine
import org.fidata.packer.engine.annotations.AutoImplement
import org.fidata.packer.engine.annotations.Inline
import groovy.transform.CompileStatic
import com.github.hashicorp.packer.template.PostProcessor

/**
 * {@code shell-local} post-processor.
 *
 * The shell-local Packer post processor enables users to do some post processing
 * after artifacts have been built
 */
@AutoImplement
@CompileStatic
abstract class ShellLocal extends PostProcessor<ShellLocal> {
  /**
   * Common configuration
   *
   * @return common configuration
   */
  @Inline
  abstract com.github.hashicorp.packer.common.ShellLocal getConfig()

  /**
   * Registers this class in specified Engine
   *
   * @param engine Engine to register in
   */
  static void register(AbstractEngine engine) {
    engine.registerSubtype PostProcessor, 'shell-local', this
  }
}
