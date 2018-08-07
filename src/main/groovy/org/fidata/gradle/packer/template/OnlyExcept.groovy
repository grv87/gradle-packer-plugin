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
import org.fidata.gradle.packer.template.internal.TemplateObject
import org.fidata.gradle.packer.template.types.TemplateString

@CompileStatic
class OnlyExcept extends TemplateObject {
  List<TemplateString> only
  List<TemplateString> except



  boolean skip(String n) {
    if (only.size() > 0) {
      if (only.contains(n)) { return false }
      return true
    }
    // TOTEST: if
    if (except.size() > 0) {
      if (only.contains(n)) { return true }
      return false
    }
    false
  }

  @Override
  protected void doInterpolate(Context ctx) {

  }
}