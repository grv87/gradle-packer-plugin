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

import static org.fidata.gradle.packer.utils.StringUtils.stringize
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

  Closure configureClosure(Closure taskConfiguration) { // TODO
    { PackerWrapperTask packerWrapperTask ->
      packerWrapperTask.environment environment
      ((PackerVarArgument)packerWrapperTask).variables = variables
      packerWrapperTask.configure taskConfiguration
    }
  }

  Object template(String name, File file, boolean oneTaskPerBuild = true, Closure taskConfiguration = null) {
    project.logger.debug(sprintf('org.fidata.packer: Processing %s template', [file]))

    Template template = Template.readFromFile(file)
    template.interpolate new Context(stringize(variables), stringize(environment), null, file, null)

    String aName = name ?: template.variablesContext.userVariables['name'] ?: file.toPath().fileName.toString()

    TaskProvider<PackerValidate> validateProvider = project.tasks.register("$PackerBasePlugin.PACKER_VALIDATE_TASK_NAME-$name".toString(), PackerValidate) { PackerValidate validate ->
      validate.templateFile = file
      validate.configure configureClosure(taskConfiguration)
    }
    project.plugins.getPlugin(PackerBasePlugin).packerValidateProvider.configure { Task packerValidate ->
      packerValidate.dependsOn validateProvider
    }

    if (oneTaskPerBuild) {
      template.builders.collect { Builder builder ->
        String buildName = builder.header.buildName
        project.tasks.register("packerBuild-$aName-$buildName".toString(), PackerBuildAutoConfigurable, file, template/*.clone() TODO*/, new OnlyExcept(only: [buildName]), configureClosure(taskConfiguration))
      }
    } else {
      project.tasks.register("packerBuild-$aName".toString(), PackerBuildAutoConfigurable, file, template, new OnlyExcept(), configureClosure(taskConfiguration))
    }
  }

  Object template(File file, boolean oneTaskPerBuild = true, Closure taskConfiguration = null) {
    template(null, file, oneTaskPerBuild, taskConfiguration)
  }

  Object template(Map<String, File> files, boolean oneTaskPerBuild = true, Closure taskConfiguration = null) {
    files.collectMany { Map.Entry<String, File> fileEntry ->
      template(fileEntry.key, fileEntry.value, oneTaskPerBuild, taskConfiguration)
    }
  }

  Object template(List<File> files, boolean oneTaskPerBuild = true, Closure taskConfiguration = null) {
    files.collectMany { File file ->
      template(file, oneTaskPerBuild, taskConfiguration)
    }
  }
}
