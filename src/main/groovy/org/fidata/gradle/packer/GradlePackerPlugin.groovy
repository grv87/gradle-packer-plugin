#!/usr/bin/env groovy
/*
 * GradlePackerPlugin class
 * Copyright © 2016-2018  Basil Peace
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
import org.gradle.api.Plugin
import org.gradle.api.Task

/**
 * `org.fidata.packer` plugin
 */
@CompileStatic
class GradlePackerPlugin implements Plugin<Project> {
  void apply(Project project) {
    if (project.tasks.findByPath('validate') == null) {
      project.task('validate') { Task task -> task.group = 'Validate' }
    }
    project.extensions.create('packer', PackerPluginExtension, project)
  }
}
