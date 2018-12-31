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

import static org.gradle.language.base.plugins.LifecycleBasePlugin.BUILD_GROUP
import static org.gradle.language.base.plugins.LifecycleBasePlugin.ASSEMBLE_TASK_NAME
import groovy.transform.PackageScope
import org.apache.commons.io.FilenameUtils
import org.fidata.gradle.packer.tasks.PackerValidateAutoConfigurable
import org.fidata.gradle.packer.tasks.PackerWrapperTask
import org.fidata.packer.engine.AbstractEngine
import org.fidata.packer.engine.Mutability
import com.github.hashicorp.packer.template.Builder
import com.github.hashicorp.packer.template.Context
import com.github.hashicorp.packer.template.OnlyExcept
import com.github.hashicorp.packer.template.Template
import groovy.transform.CompileStatic
import org.fidata.gradle.packer.tasks.AbstractPackerBuild
import org.fidata.gradle.packer.tasks.PackerBuildAutoConfigurable
import org.fidata.gradle.packer.tasks.AbstractPackerValidate
import org.gradle.api.Project
import org.gradle.api.Plugin
import org.gradle.api.Task
import org.gradle.api.tasks.TaskProvider

/**
 * `org.fidata.packer` plugin
 *
 * TODO: No concept of subproject is supported
 */
@CompileStatic
class PackerPlugin implements Plugin<Project> {
  // internal
  static final String PACKER_EXTENSION_NAME = 'packer'

  static final String PACKER_BUILD_TASK_NAME = 'packerBuild'

  private PackerBasePlugin basePlugin

  @PackageScope
  Project project

  AbstractEngine<Template> getEngine() {
    this.@basePlugin.engine
  }

  TaskProvider<Task> getPackerValidateProvider() {
    this.@basePlugin.packerValidateProvider
  }

  private PackerPluginExtension extension

  void apply(Project project) {
    this.@project = project
    this.@basePlugin = project.plugins.apply(PackerBasePlugin)

    // TOTEST that this is not affect configuration-on-demand
    // TOTHINK
    /*project.allprojects { Project childProject ->
      childProject.pluginManager.apply LifecycleBasePlugin
      childProject.extensions.create PACKER_EXTENSION_NAME, PackerPluginExtension, this, project
    }*/
    extension = project.extensions.create(PACKER_EXTENSION_NAME, PackerPluginExtension, this)
  }

  @PackageScope
  void addTemplate(Object templatePath, String name = null) { // TODO: support custom configure closure
    File templateFile = this.@project.file(templatePath)
    File defaultWorkingDir = templateFile.parentFile

    Template template = Template.readFromFile(engine, templateFile, Mutability.IMMUTABLE)
    template.interpolate new Context(null, null, templateFile, defaultWorkingDir.toPath())

    String aName = name ?: template.variablesCtx.templateName ?: FilenameUtils.getBaseName(templateFile.toString()) // MARK1

    TaskProvider<AbstractPackerValidate> validateProvider = project.tasks.register("$PackerBasePlugin.PACKER_VALIDATE_TASK_NAME-$aName", PackerValidateAutoConfigurable,
      templateFile
    ) { AbstractPackerValidate validate ->
      validate.variables.putAll extension.variables
      validate.env.putAll extension.env
      validate.workingDir.set extension.workingDir
      validate.syntaxOnly.set true
    }
    packerValidateProvider.configure { Task packerValidate ->
      packerValidate.dependsOn validateProvider
    }

    template.builders.each { Builder builder ->
      String buildName = builder.header.buildName // TODO: replace : and path characters in buildName; toSafeFileName

      TaskProvider<AbstractPackerBuild> buildProvider = project.tasks.register("$PACKER_BUILD_TASK_NAME-$aName-$buildName", PackerBuildAutoConfigurable,
        template,
        OnlyExcept.only([buildName])
      ) { AbstractPackerBuild build ->
        build.group = BUILD_GROUP
        build.variables.putAll extension.variables
        build.env.putAll extension.env
        build.workingDir.set extension.workingDir
      }
      buildProvider.configure { AbstractPackerBuild build ->
        build.shouldRunAfter validateProvider
      }
      // TOTHINK
      // 1) Need for this 2) Assemble task
      project.tasks.named(ASSEMBLE_TASK_NAME).configure { Task assemble ->
        assemble.dependsOn buildProvider
      }
    }
  }

  private static Closure configureClosure() {
    { PackerWrapperTask packerWrapperTask ->


    }
  }
}
