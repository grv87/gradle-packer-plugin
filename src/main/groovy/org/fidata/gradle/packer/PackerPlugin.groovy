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

import static org.gradle.language.base.plugins.LifecycleBasePlugin.BUILD_TASK_NAME
import static org.gradle.language.base.plugins.LifecycleBasePlugin.CHECK_TASK_NAME
import static org.gradle.internal.impldep.org.apache.commons.io.FilenameUtils.removeExtension
import static org.gradle.internal.impldep.org.apache.commons.io.FileUtils.iterateFiles
import com.github.hashicorp.packer.template.Builder
import com.github.hashicorp.packer.template.Context
import com.github.hashicorp.packer.template.OnlyExcept
import com.github.hashicorp.packer.template.Template
import groovy.transform.CompileStatic
import org.fidata.gradle.packer.internal.PackerPluginsSharedData
import org.fidata.gradle.packer.tasks.AbstractPackerBuild
import org.fidata.gradle.packer.tasks.PackerBuildAutoConfigurable
import org.fidata.gradle.packer.tasks.PackerValidate
import org.fidata.gradle.packer.tasks.arguments.PackerVarArgument
import org.gradle.api.Project
import org.gradle.api.Plugin
import org.gradle.api.Task
import org.gradle.api.file.Directory
import org.gradle.api.initialization.ProjectDescriptor
import org.gradle.api.initialization.Settings
import org.gradle.api.plugins.ExtensionAware
import org.gradle.api.plugins.ExtensionContainer
import org.gradle.api.tasks.TaskProvider
import org.gradle.language.base.plugins.LifecycleBasePlugin

/**
 * `org.fidata.packer` plugin
 */
@CompileStatic
class PackerPlugin implements Plugin<Project>, Plugin<Settings> {
  static final String PACKER_EXTENSION_NAME = 'packer'

  static final String PACKER_BUILD_TASK_NAME = 'packerBuild'

  void apply(Project project) {
    project.pluginManager.apply PackerBasePlugin
    TaskProvider<Task> packerValidateProvider  = project.plugins.getPlugin(PackerBasePlugin).packerValidateProvider

    project.allprojects { Project childProject ->
      childProject.pluginManager.apply LifecycleBasePlugin
      childProject.extensions.create PACKER_EXTENSION_NAME, PackerPluginExtension, childProject
    }

    // TODO: https://github.com/gradle/gradle/issues/5546 ?
    PackerPluginsSharedData sharedData = ((ExtensionAware) (project.gradle)).extensions.findByType(PackerPluginsSharedData)
    if (!sharedData) {
      throw new IllegalStateException('Packer plugins shared data is not found. You should apply org.fidata.packer plugin to Settings too.')
    }

    PackerPluginsSharedData.SourceSetDescriptor sourceSet = sharedData.sourceSets[project.path]
    if (!sourceSet) {
      throw new IllegalStateException('Packer plugins shared data is not found. You should apply org.fidata.packer plugin to Settings too.')
    }

    sourceSet.templateDescriptors.each { PackerPluginsSharedData.SourceSetDescriptor.TemplateDescriptor templateDescriptor ->
      Project subproject = project.project(templateDescriptor.projectPath)
      TaskProvider<PackerValidate> validateProvider = subproject.tasks.register(PackerBasePlugin.PACKER_VALIDATE_TASK_NAME, PackerValidate) { PackerValidate validate ->
        validate.templateFile.set templateDescriptor.template.path.toFile()
        validate.syntaxOnly.set true
        validate.configure configureClosure(project, subproject.layout.projectDirectory/*.dir(templateDescriptor.template.path.parent.toAbsolutePath().toString())*/)
      }
      subproject.plugins.withType(LifecycleBasePlugin) {
        project.tasks.named(CHECK_TASK_NAME).configure { Task check ->
          check.dependsOn validateProvider
        }
      }
      packerValidateProvider.configure { Task packerValidate ->
        packerValidate.dependsOn validateProvider
      }
    }

    sourceSet.buildDescriptors.each { PackerPluginsSharedData.SourceSetDescriptor.BuildDescriptor buildDescriptor ->
      Project subproject = project.project(buildDescriptor.projectPath)
      TaskProvider<AbstractPackerBuild> buildProvider = subproject.tasks.register(PACKER_BUILD_TASK_NAME, PackerBuildAutoConfigurable,
        buildDescriptor.template.clone(),
        OnlyExcept.only([buildDescriptor.buildName]),
        configureClosure(project, subproject.layout.projectDirectory)
      )
      subproject.plugins.withType(LifecycleBasePlugin) {
        project.tasks.named(BUILD_TASK_NAME).configure { Task build ->
          build.dependsOn buildProvider
        }
      }
    }
  }

  private static Closure configureClosure(Project rootProject, Directory templateDir) { // TODO
    { PackerVarArgument packerWrapperTask ->
      packerWrapperTask.workingDir.set templateDir

      Stack<Project> projects = new Stack<Project>()
      Project project = packerWrapperTask.project
      while (project) {
        projects.push project
        if (project == rootProject) {
          break
        }
        project = project.parent
      }
      while (projects) {
        project = projects.pop()
        PackerPluginExtension extension = project.extensions.getByType(PackerPluginExtension)
        packerWrapperTask.environment extension.environment
        if (extension.workingDir.present) {
          packerWrapperTask.workingDir.set extension.workingDir
        }
        packerWrapperTask.variables = extension.variables
      }
    }
  }

  // internal
  private static final String PACKER_PLUGIN_SHARED_DATA_EXTENSION_NAME = 'packer'

  void apply(Settings settings) {
    PackerPluginsSharedData.SourceSetDescriptor sourceSetDescriptor = new PackerPluginsSharedData.SourceSetDescriptor()

    // TOTHINK: use `FileVisitor` to get pure `Path`s
    iterateFiles(settings.rootDir, ['json'].toArray(new String[1]), true) { File templateFile ->
      // TODO: Settings don't have logger ?
      // settings.logger.debug(sprintf('org.fidata.packer: Processing %s template', [templateFile]))
      File templateDir = templateFile.parentFile
      String templateName = removeExtension(templateFile.toPath().fileName.toString())
      String projectPath = /* TOTEST settings.rootDir.toPath().relativize( or Project.relativePath*/templateDir.toPath().resolve(templateName)/*)*/.toString().replace(File.separatorChar, ':' as char)

      settings.include projectPath
      ProjectDescriptor projectDescriptor = settings.project(projectPath)
      projectDescriptor.projectDir = templateDir
      projectDescriptor.buildFileName = "${ templateName }.gradle"

      Template template = Template.readFromFile(templateFile)
      template.interpolate new Context(null, null, templateFile, settings.settingsDir.toPath())

      sourceSetDescriptor.templateDescriptors.add new PackerPluginsSharedData.SourceSetDescriptor.TemplateDescriptor(projectPath, template)

      // TOTHINK: String aName = name ?: template.variablesCtx.templateName ?: file.toPath().fileName.toString()

      template.builders.each { Builder builder ->
        String buildName = builder.header.buildName // TODO: replace : and path characters in buildName; toSafeFilename
        String subprojectPath = "$projectPath:$buildName"

        settings.include subprojectPath
        ProjectDescriptor subpprojectDescriptor = settings.project(subprojectPath)
        subpprojectDescriptor.projectDir = templateDir
        subpprojectDescriptor.buildFileName = "$templateName-${ buildName }.gradle"

        sourceSetDescriptor.buildDescriptors.add new PackerPluginsSharedData.SourceSetDescriptor.BuildDescriptor(projectPath, template, buildName)
      }
      // projectDescriptor.path

      // TODO: https://github.com/gradle/gradle/issues/5546 ?
      ExtensionContainer extensions = ((ExtensionAware)settings.gradle).extensions
      PackerPluginsSharedData sharedData = extensions.findByType(PackerPluginsSharedData)
      if (!sharedData) {
        sharedData = new PackerPluginsSharedData()
        extensions.add PACKER_PLUGIN_SHARED_DATA_EXTENSION_NAME, sharedData
      }
      sharedData.sourceSets[settings.rootProject.path] = sourceSetDescriptor // TOTEST
    }
  }
}
