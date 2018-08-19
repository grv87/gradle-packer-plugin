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

import groovy.transform.CompileStatic
import org.fidata.gradle.packer.tasks.PackerBuildAutoConfigurable
import org.fidata.gradle.packer.tasks.PackerValidate
import org.fidata.gradle.packer.tasks.PackerWrapperTask
import org.fidata.gradle.packer.tasks.arguments.PackerVarArgument
import org.fidata.gradle.packer.template.Builder
import org.fidata.gradle.packer.template.Context
import org.fidata.gradle.packer.template.OnlyExcept
import org.fidata.gradle.packer.template.Template
import org.gradle.api.Project
import org.gradle.api.Task
import org.gradle.api.tasks.TaskProvider
import org.ysb33r.grolifant.api.StringUtils
import org.ysb33r.grolifant.api.exec.AbstractToolExtension

/**
 * {@code packer} extension for Gradle project
 */
@CompileStatic
class PackerPluginExtension /*extends PackerToolExtension*/ {
  private final Project project

  Map<String, Object> environment = [:]
  Map<String, Object> variables = [:]

  PackerPluginExtension(Project project) {
    this.project = project
  }

  Closure configureClosure(Closure taskConfiguration) {
    { PackerWrapperTask packerWrapperTask ->
      environment?.each { Map.Entry<String, Object> environmentEntry ->
        packerWrapperTask.environment.put environmentEntry.key, StringUtils.stringize(environmentEntry.value)
      }
      ((PackerVarArgument)packerWrapperTask).variables = variables // TODO
      packerWrapperTask.configure taskConfiguration
    }
  }

  static final Map<String, String> stringize(Map<? extends Object, ? extends Object> stringyThings) {
    (Map<String, String>)stringyThings.collectEntries { Map.Entry<? extends Object, ? extends Object> entry -> [(StringUtils.stringize(entry.key)): StringUtils.stringize(entry.value)] }
  }

  void template(String name, File file, Closure taskConfiguration = null) {
    project.logger.debug(sprintf('org.fidata.packer: Processing %s template', [file]))

    Template template = Template.readFromFile(file)
    template.interpolate new Context(stringize(variables), stringize(environment), null, file, null)

    if (!name) {
      name = template.variablesContext.userVariables['name'] ?: file.toPath().fileName.toString()
    }

    TaskProvider<PackerValidate> validateProvider = project.tasks.register("$PackerBasePlugin.PACKER_VALIDATE_TASK_NAME-$name".toString(), PackerValidate) { PackerValidate validate ->
      validate.templateFile = file
      validate.configure taskConfiguration
    }
    project.plugins.getPlugin(PackerBasePlugin).packerValidateProvider.configure { Task packerValidate ->
      packerValidate.dependsOn validateProvider
    }

    for (/*Map.Entry<String, Builder>*/ Builder builder in template.builders) {
      String buildName = builder.header.buildName
      TaskProvider<PackerBuildAutoConfigurable> buildProvider = project.tasks.register("packerBuild-$name-$buildName".toString(), PackerBuildAutoConfigurable, file, template, new OnlyExcept(only: [buildName]), configureClosure(taskConfiguration))
    }
  }

  void template(File file, Closure taskConfiguration = null) {
    template(null, file, taskConfiguration)
  }

  void template(Map<String, File> files, Closure taskConfiguration = null) {
    for (Map.Entry<String, File> fileEntry : files) {
      template(fileEntry.key, fileEntry.value, taskConfiguration)
    }
  }

  void template(List<File> files, Closure taskConfiguration = null) {
    for (File file : files) {
      template(file, taskConfiguration)
    }
  }
}
