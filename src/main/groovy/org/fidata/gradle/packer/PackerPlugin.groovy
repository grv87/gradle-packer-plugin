#!/usr/bin/env groovy
/*
 * PackerPlugin class
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

/**
 * `org.fidata.packer` plugin
 */
@CompileStatic
class PackerPlugin implements Plugin<Project> {
  static final String PACKER_EXTENSION_NAME = 'packer'
  void apply(Project project) {
    project.pluginManager.apply PackerBasePlugin
    project.extensions.create(PACKER_EXTENSION_NAME, PackerPluginExtension, project)
  }
}
