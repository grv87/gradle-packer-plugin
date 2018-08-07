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
import org.fidata.gradle.packer.PackerBuild
import org.fidata.gradle.packer.PackerToolExtension
import org.fidata.gradle.packer.PackerValidate
import org.fidata.gradle.packer.template.Builder
import org.fidata.gradle.packer.template.Context
import org.fidata.gradle.packer.template.OnlyExcept
import org.fidata.gradle.packer.template.Template
import org.fidata.gradle.packer.template.types.TemplateString
import org.gradle.api.Project
import org.gradle.api.Task
import org.gradle.api.tasks.TaskProvider
import org.ysb33r.grolifant.api.StringUtils
import com.fasterxml.jackson.databind.ObjectMapper

/**
 * {@code packer} extension for Gradle project
 */
@CompileStatic
class PackerPluginExtension extends PackerToolExtension {
  Map<String, Object> environment = [:]
  Map<String, Object> variables = [:]

  Closure configureClosure(Closure taskConfiguration) {
    { PackerWrapperTask packerWrapperTask ->
      environment?.each { Map.Entry<String, Object> environmentEntry ->
        packerWrapperTask.environment.put environmentEntry.key, StringUtils.stringize(environmentEntry.value)
      }
      packerWrapperTask.variables = variables
      packerWrapperTask.configure taskConfiguration
    }
  }

  void template(String name, File file, Task parentTask = null, Closure taskConfiguration = null) {
    project.logger.debug(sprintf('gradle-packer-plugin: Processing %s template', [file]))
    ObjectMapper mapper = new ObjectMapper()
    Template template = mapper.readValue(file.text, Template)
    if (!name) {
      // name = template.variables.getOrDefault('name', null)?.interpolateForGradle(null) ?: file.toPath().fileName.toString() TODO
    }

    TaskProvider<PackerValidate> validateProvider = project.tasks.register("$PackerBasePlugin.PACKER_VALIDATE_TASK_NAME-$name".toString(), PackerValidate, file, configureClosure(taskConfiguration))

    TaskProvider<PackerBuild> buildAllProvider = project.tasks.register("packerBuild-$name".toString(), PackerBuild, file, template, configureClosure(taskConfiguration))
    parentTask?.dependsOn buildAllProvider

    Context ctx = new Context()
    template.variables?.each { Map.Entry<String, String> variable ->
      ctx.userVariables[variable.key] = variable.value
    }
    for (/*Map.Entry<String, Builder>*/ Builder builder in template.builders) {
      // builder.header.interpolate() TODO
      String buildName =
        name ?: builder.header.type // TODO: interpolate
      TaskProvider<PackerBuild> buildProvider = project.tasks.register("packerBuild-$name-$buildName".toString(), PackerBuild, file, template, /*new OnlyExcept(only: [new TemplateString(buildName)]), TODO*/ configureClosure(taskConfiguration))
    }
  }

  void template(File file, Task parentTask = null, Closure taskConfiguration = null) {
    template(null, file, parentTask, taskConfiguration)
  }

  void template(Map<String, File> files, Task parentTask = null, Closure taskConfiguration = null) {
    for(Map.Entry<String, File> fileEntry : files) {
      template(fileEntry.key, fileEntry.value, parentTask, taskConfiguration)
    }
  }

  void template(List<File> files, Task parentTask = null, Closure taskConfiguration = null) {
    for(File file : files) {
      template(file, parentTask, taskConfiguration)
    }
  }

  PackerPluginExtension(Project project) {
    super(project)
  }
}
