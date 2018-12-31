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

import static org.gradle.language.base.plugins.LifecycleBasePlugin.CHECK_TASK_NAME
import static org.gradle.language.base.plugins.LifecycleBasePlugin.VERIFICATION_GROUP
import com.google.common.collect.ImmutableList
import org.fidata.gradle.packer.tasks.PackerValidate
import com.github.hashicorp.packer.template.Template
import org.fidata.packer.engine.AbstractEngine
import org.fidata.gradle.packer.tasks.PackerBuild
import org.fidata.gradle.packer.tasks.AbstractPackerValidate
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
  private PackerEnginePlugin enginePlugin

  static final String PACKER_VALIDATE_TASK_NAME = 'packerValidate'

  private TaskProvider<Task> packerValidateProvider

  TaskProvider<Task> getPackerValidateProvider() {
    this.@packerValidateProvider
  }

  private static final List<Class<Task>> TASK_CLASSES  = ImmutableList.<Task>of(PackerBuild, PackerValidate)

  AbstractEngine<Template> getEngine() {
    this.@enginePlugin.engine
  }

  void apply(Project project) {
    this.@enginePlugin = project.gradle.plugins.apply(PackerEnginePlugin)

    project.pluginManager.apply LifecycleBasePlugin

    TASK_CLASSES.each { Class<Task> taskClass ->
      project.extensions.extraProperties[taskClass.simpleName] = taskClass
    }

    packerValidateProvider = project.tasks.register(PACKER_VALIDATE_TASK_NAME) { Task packerValidate ->
      packerValidate.group = VERIFICATION_GROUP
      packerValidate.dependsOn project.tasks.withType(AbstractPackerValidate)
    }
    project.plugins.withType(LifecycleBasePlugin) {
      project.tasks.named(CHECK_TASK_NAME).configure { Task check ->
        check.dependsOn packerValidateProvider
      }
    }

    project.extensions.create(PackerToolExtension.NAME, PackerToolExtension, project)
  }
}
