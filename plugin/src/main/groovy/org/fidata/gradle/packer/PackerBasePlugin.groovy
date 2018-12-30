#!/usr/bin/env groovy
/*
 * PackerBasePlugin class
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
package org.fidata.gradle.packer

import org.fidata.packer.engine.AbstractEngine

import static org.gradle.language.base.plugins.LifecycleBasePlugin.CHECK_TASK_NAME
import static org.gradle.language.base.plugins.LifecycleBasePlugin.VERIFICATION_GROUP

import org.fidata.gradle.packer.tasks.PackerBuild
import org.fidata.gradle.packer.tasks.PackerValidate
import groovy.transform.CompileStatic
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.Task
import org.gradle.api.tasks.TaskProvider
import org.gradle.language.base.plugins.LifecycleBasePlugin

/**
 * `org.fidata.packer-base` plugin
 */
@CompileStatic
class PackerBasePlugin implements Plugin<Project> {
  private final AbstractEngine engine = new AbstractEngine()

  static final String PACKER_VALIDATE_TASK_NAME = 'packerValidate'

  private TaskProvider<Task> packerValidateProvider

  TaskProvider<Task> getPackerValidateProvider() {
    this.packerValidateProvider
  }

  void apply(Project project) {
    project.pluginManager.apply LifecycleBasePlugin

    for (Class taskClass : [PackerBuild, PackerValidate]) {
      project.extensions.extraProperties[taskClass.simpleName] = taskClass
    }
    packerValidateProvider = project.tasks.register(PACKER_VALIDATE_TASK_NAME) { Task packerValidate ->
      packerValidate.group = VERIFICATION_GROUP
      packerValidate.dependsOn project.tasks.withType(PackerValidate)
    }
    project.plugins.withType(LifecycleBasePlugin) {
      project.tasks.named(CHECK_TASK_NAME).configure { Task check ->
        check.dependsOn packerValidateProvider
      }
    }
    project.extensions.create(PackerToolExtension.NAME, PackerToolExtension, project)
  }
}
