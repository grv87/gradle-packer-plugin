/*
 * ShellLocal post-processor
 * Copyright ©  Basil Peace
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
 * file post-processor/shell-local/post-processor.go
 * under the terms of the Mozilla Public License, v. 2.0.
 */
package com.github.hashicorp.packer.postprocessor

import com.github.hashicorp.packer.common.shelllocal.ShellLocalConfig
import com.github.hashicorp.packer.packer.Artifact
import org.fidata.packer.engine.AbstractEngine
import org.fidata.packer.engine.PostProcessResult
import org.fidata.packer.engine.annotations.AutoImplement
import org.fidata.packer.engine.annotations.Inline
import groovy.transform.CompileStatic
import com.github.hashicorp.packer.template.PostProcessor

/**
 * {@code shell-local} post-processor.
 *
 * The local shell post processor executes scripts locally during the post
 * processing stage. Shell local provides a convenient way to automate executing
 * some task with packer outputs and variables.
 */
@AutoImplement(name = 'shell-local')
@CompileStatic
abstract class ShellLocal extends PostProcessor<ShellLocal> {
  /**
   * Common configuration
   *
   * @return common configuration
   */
  @Inline
  abstract ShellLocalConfig getConfig()

  @Override
  protected final PostProcessResult doPostProcess(Artifact priorArtifact) {
    new PostProcessResult(
      priorArtifact,
      Boolean.TRUE,
      Collections.EMPTY_LIST
    )
  }
}
