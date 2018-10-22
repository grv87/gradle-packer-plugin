/*
 * OnlyExcept class
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
package com.github.hashicorp.packer.template

import groovy.transform.AutoClone
import groovy.transform.AutoCloneStyle
import groovy.transform.CompileStatic

@AutoClone(style = AutoCloneStyle.SIMPLE)
@CompileStatic
class OnlyExcept {
  List<String> only
  List<String> except

  boolean skip(String n) {
    if (only?.size() > 0) {
      return !only.contains(n)
    }
    // TOTEST: if
    if (except?.size() > 0) {
      return except.contains(n)
    }
    false
  }

  int sizeAfterSkip(int originalSize) {
    only?.size() > 0 ? only.size() : originalSize - except?.size() ?: 0
  }
}
