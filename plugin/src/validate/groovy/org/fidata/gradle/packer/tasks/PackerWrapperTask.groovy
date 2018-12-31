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

import static org.ysb33r.grolifant.api.StringUtils.stringize
import org.gradle.api.file.Directory
import groovy.transform.CompileStatic
import org.fidata.gradle.packer.PackerExecSpec
import org.fidata.gradle.packer.PackerToolExtension
import org.fidata.gradle.packer.tasks.arguments.PackerArgument
import org.fidata.gradle.packer.tasks.arguments.PackerTemplateReadOnlyArgument
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.provider.MapProperty
import org.gradle.api.tasks.Internal
import org.ysb33r.grolifant.api.exec.AbstractExecWrapperTask

@CompileStatic
abstract class PackerWrapperTask extends AbstractExecWrapperTask<PackerExecSpec, PackerToolExtension> implements PackerArgument {
  @Internal
  final MapProperty<String, String> env = project.objects.mapProperty(String, String).empty()

  @Override
  void setEnvironment(Map<String, ?> args) {
    env.empty()
    environment args
  }

  @Internal
  @Override
  Map<String, String> getEnvironment() {
    env.get()
  }

  @Override
  void environment(Map<String, ?> args) {
    args.each { String key, Object value ->
      env.put key, project.provider({ stringize(value) })
    }
  }

  @Internal
  final DirectoryProperty workingDir = project.objects.directoryProperty().convention project.provider({
    if (PackerTemplateReadOnlyArgument.isInstance(this)) {
      DirectoryProperty directoryProperty = project.objects.directoryProperty()
      directoryProperty.set(((PackerTemplateReadOnlyArgument)this).templateFile.get().asFile.parentFile)
      Directory directory = directoryProperty.get()
      if (directory != null) {
        return directory
      }
    }
    project.layout.projectDirectory
  })

  @Lazy
  private final PackerToolExtension packerToolExtension = extensions.create(PackerToolExtension.NAME, PackerToolExtension, this)

  @Override
  @Internal // @Nested TODO: Detect version ? / plugins ?
  protected PackerToolExtension getToolExtension() {
    this.packerToolExtension
  }

  @Override
  protected PackerExecSpec createExecSpec() {
    new PackerExecSpec(project, toolExtension.resolver)
  }

  @Override
  protected PackerExecSpec configureExecSpec(PackerExecSpec execSpec) {
    addEnvironmentToExecSpec execSpec
    execSpec.workingDir workingDir
    execSpec.cmdArgs cmdArgs
    execSpec
  }
}
