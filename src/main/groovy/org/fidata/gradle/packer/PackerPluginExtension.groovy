#!/usr/bin/env groovy
/*
 * PackerPluginExtension class
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

import org.gradle.api.file.DirectoryProperty
import groovy.transform.CompileStatic
import org.gradle.api.Project

/**
 * {@code packer} extension for Gradle project
 */
@CompileStatic
class PackerPluginExtension /*extends PackerToolExtension*/ {
  private final Project project

  /* TODO: use map of Provider<String> ? */
  Map<String, Object> environment = [:]
  Map<String, Object> variables = [:]

  final DirectoryProperty workingDir = project.layout.directoryProperty()

  PackerPluginExtension(Project project) {
    this.project = project
  }
}
