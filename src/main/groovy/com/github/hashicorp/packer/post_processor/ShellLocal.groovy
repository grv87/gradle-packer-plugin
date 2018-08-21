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
package com.github.hashicorp.packer.post_processor

import com.fasterxml.jackson.annotation.JsonUnwrapped
import groovy.transform.AutoClone
import groovy.transform.AutoCloneStyle
import groovy.transform.CompileStatic
import com.github.hashicorp.packer.template.PostProcessor
import org.gradle.api.tasks.Nested

@AutoClone(style = AutoCloneStyle.SIMPLE)
@CompileStatic
class ShellLocal extends PostProcessor {
  @JsonUnwrapped
  @Nested
  com.github.hashicorp.packer.common.ShellLocal config
}
