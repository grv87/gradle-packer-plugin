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
import org.gradle.api.model.ObjectFactory
import org.gradle.api.provider.MapProperty
import javax.inject.Inject

/**
 * {@code packer} extension for Gradle project
 */
@CompileStatic
class PackerPluginExtension /*extends PackerToolExtension*/ {
  private final PackerPlugin plugin

  final MapProperty<String, String> env

  final DirectoryProperty workingDir

  final MapProperty<String, String> variables

  void template(Object templateFile, String name = null) {
    plugin.addTemplate templateFile
  }

  void templates(Object... templateFiles) {
    templateFiles.each { Object templateFile ->
      plugin.addTemplate templateFile
    }
  }

  void templates(Iterable<Object> templateFiles) {
    templateFiles.each { Object templateFile ->
      plugin.addTemplate templateFile
    }
  }

  void templateDir(Object templateDir) {
    plugin.project.fileTree(dir: templateDir, include: '*.json').each { File file ->
      template file
    }
  }

  @Inject
  PackerPluginExtension(PackerPlugin plugin) {
    this.@plugin = plugin

    ObjectFactory objects = plugin.project.objects
    env = objects.mapProperty(String, String).empty()
    workingDir = objects.directoryProperty()
    variables = objects.mapProperty(String, String).empty()
  }
}
