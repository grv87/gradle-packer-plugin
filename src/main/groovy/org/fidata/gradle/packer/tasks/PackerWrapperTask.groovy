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
package org.fidata.gradle.packer.tasks

import groovy.transform.CompileStatic
import org.fidata.gradle.packer.PackerExecSpec
import org.fidata.gradle.packer.PackerToolExtension
import org.fidata.gradle.packer.tasks.arguments.PackerArgument
import org.fidata.gradle.packer.tasks.arguments.PackerTemplateArgument
import org.gradle.api.tasks.Console
import org.gradle.api.tasks.Internal
import org.ysb33r.grolifant.api.StringUtils
import org.ysb33r.grolifant.api.exec.AbstractExecWrapperTask

@CompileStatic
abstract class PackerWrapperTask extends AbstractExecWrapperTask<PackerExecSpec, PackerToolExtension> implements PackerArgument {
  @Console // TODO
  Boolean machineReadable = false

  @Internal
  @Override
  List<Object> getCmdArgs() {
    List<Object> cmdArgs = PackerArgument.super.getCmdArgs()
    if (machineReadable) {
      cmdArgs.add '-machine-readable'
    }
    cmdArgs
  }

  PackerWrapperTask() {
    super()
    packerToolExtension = extensions.create(PackerToolExtension.NAME, PackerToolExtension, this)
  }

  @Override
  protected PackerExecSpec createExecSpec() {
    new PackerExecSpec(project, toolExtension.resolver)
  }

  private PackerToolExtension packerToolExtension

  @Override
  @Internal // @Nested TODO: Detect version ? / plugins ?
  protected PackerToolExtension getToolExtension() {
    this.packerToolExtension
  }

  @Override
  protected PackerExecSpec configureExecSpec(PackerExecSpec execSpec) {
    execSpec.cmdArgs cmdArgs
    execSpec
  }
}
