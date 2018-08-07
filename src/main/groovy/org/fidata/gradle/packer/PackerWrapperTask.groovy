/*
 * PackerWrapperTask class
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

import groovy.transform.CompileStatic
import org.gradle.api.logging.LogLevel
import org.gradle.api.tasks.Internal
import org.ysb33r.grolifant.api.exec.AbstractExecWrapperTask

@CompileStatic
abstract class PackerWrapperTask extends AbstractExecWrapperTask<PackerExecSpec, PackerToolExtension> {
  private File templateFile
  @Internal // TODO
  File getTemplateFile() {
    templateFile
  }

  @Internal
  Map<String, Object> variables

  PackerWrapperTask(File templateFile) {
    super()
    packerToolExtension = extensions.create(PackerToolExtension.NAME, PackerToolExtension, this)
    this.templateFile = templateFile
  }

  @Override
  protected PackerExecSpec createExecSpec() {
    new PackerExecSpec(project, getToolExtension().getResolver())
  }

  @Override
  protected PackerExecSpec configureExecSpec(PackerExecSpec execSpec) {
    for (Map.Entry<String, Object> variable in variables) {
      execSpec.cmdArgs.push '-var'
      execSpec.cmdArgs.push "${ variable.key }=${ variable.value }".toString()
    }
    if ((project.logging.level ?: project.gradle.startParameter.logLevel) <= LogLevel.DEBUG) {
      execSpec.cmdArgs.push '-debug'
    }
    execSpec.cmdArgs templateFile.toString() // TODO: Should be the last
    execSpec
  }

  private PackerToolExtension packerToolExtension

  @Override
  protected PackerToolExtension getToolExtension() {
    /*TODO this.*/packerToolExtension
  }
}