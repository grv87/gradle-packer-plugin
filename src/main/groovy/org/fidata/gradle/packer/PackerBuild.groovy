/*
 * PackerBuild class
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
import org.fidata.gradle.packer.template.Context
import org.fidata.gradle.packer.template.OnlyExcept
import org.fidata.gradle.packer.template.Template
import org.gradle.api.provider.Provider
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.Internal
import org.gradle.api.tasks.Nested
import org.gradle.api.tasks.Optional

import javax.inject.Inject

@CompileStatic
class PackerBuild extends PackerWrapperTask {
  private Template template

  @Internal
  Template getTemplate() {
    this.template
  }

  @Nested
  Provider<List<Template>> getInterpolatedTemplates() {
    Context ctx = new Context()



    /*for (Builder builder in template.builders) {
      if (onlyExcept.skip(builder.header.name))
    }
    template.clone()*/
    null
  }

  private OnlyExcept onlyExcept

  @Input
  @Optional
  OnlyExcept getOnlyExcept() {
    onlyExcept
  }

  @Inject
  PackerBuild(File templateFile, Template template, OnlyExcept onlyExcept = null, Closure configureClosure = null) {
    super(templateFile)
    group = 'Build' // TODO: constant
    this.template = template
    this.onlyExcept = onlyExcept
    configure configureClosure
    doConfigure() // TODO ?
  }

  protected void doConfigure() {
    // TODO
  }

  @Override
  protected PackerExecSpec configureExecSpec(PackerExecSpec execSpec) {
    super.configureExecSpec(execSpec)
    execSpec.command 'build'
    execSpec
  }

  @Override
  @Internal
  protected List<Object> getCmdArgs() {
    if (onlyExcept) {
      if (onlyExcept.only?.size() > 0) {
        [(Object)"-only=${ onlyExcept.only.join(',') }"]
      } else if (onlyExcept.except?.size() > 0) {
        [(Object)"-except=${ onlyExcept.except.join(',') }"]
      } else {
        null
      }
    } else {
      null
    }
  }
}
