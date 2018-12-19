/*
 * ShellLocal class
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
package com.github.hashicorp.packer.common

import com.github.hashicorp.packer.engine.types.base.InterpolableObject
import com.github.hashicorp.packer.engine.types.InterpolableString
import com.github.hashicorp.packer.engine.types.InterpolableStringArray
import groovy.transform.CompileStatic

@CompileStatic
class ShellLocal extends InterpolableObject {
  InterpolableStringArray inline

  InterpolableString inlineShebang

  InterpolableString tempfileExtension

  InterpolableString script

  InterpolableStringArray scripts

  InterpolableStringArray environmentVars

  InterpolableStringArray executeCommand

  InterpolableStringArray useLinuxPathing
}
