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
package org.fidata.gradle.packer.template

import groovy.transform.CompileStatic

@CompileStatic
class OnlyExcept {
  List<String> only
  List<String> except

  boolean skip(String n) {
    if (only.size() > 0) {
      if (only.contains(n)) { return false }
      return true
    }
    // TOTEST: if both only and except are provided
    if (except.size() > 0) {
      if (only.contains(n)) { return true }
      return false
    }
    false
  }
}
