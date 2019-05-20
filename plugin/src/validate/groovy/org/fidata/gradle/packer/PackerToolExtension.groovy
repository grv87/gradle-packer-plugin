/*
 * PackerToolException class
 * Copyright ©  Basil Peace
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
package org.fidata.gradle.packer

import groovy.transform.CompileStatic
import org.gradle.api.Project
import org.gradle.api.Task
import org.gradle.api.tasks.Input
import org.ysb33r.grolifant.api.OperatingSystem
import org.ysb33r.grolifant.api.exec.AbstractToolExtension
import org.ysb33r.grolifant.api.exec.ResolvableExecutable

@CompileStatic
class PackerToolExtension extends AbstractToolExtension {
  static final String NAME = 'packerToolConfig'

  PackerToolExtension(Project project) {
    super(project)
    executable path: OperatingSystem.current().findInPath('packer')
  }

  PackerToolExtension(Task task) {
    super(task, NAME)
  }

  @Override
  @Input
  ResolvableExecutable getResolvableExecutable() {
    super.resolvableExecutable
  }
}
