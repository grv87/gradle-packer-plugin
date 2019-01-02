/*
 * Vagrant post-processor
 * Copyright © 2018-2019  Basil Peace
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
 *
 * Ported from original Packer code,
 * file post-processor/vagrant/post-processor.go
 * under the terms of the Mozilla Public License, v. 2.0.
 */
package com.github.hashicorp.packer.postprocessor

import com.fasterxml.jackson.annotation.JsonProperty
import groovy.transform.CompileStatic
import com.github.hashicorp.packer.template.PostProcessor
import org.fidata.packer.engine.AbstractEngine

@CompileStatic
abstract class Vagrant extends PostProcessor<Vagrant> {
  abstract Integer getCompressionLevel()

  abstract List<String> getInclude()

  @JsonProperty('output')
  abstract String getOutputPath()

  abstract Map<String, Object> getOverride()

  abstract String getVagrantfileTemplate()

  static void register(AbstractEngine engine) {
    engine.registerSubtype PostProcessor, 'vagrant', this
  }
}
