/*
 * ShellLocal class
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
package com.github.hashicorp.packer.common

import com.fasterxml.jackson.annotation.JsonAlias
import org.fidata.packer.engine.annotations.AutoImplement
import org.fidata.packer.engine.annotations.ContextVar
import org.fidata.packer.engine.annotations.ContextVars
import org.fidata.packer.engine.annotations.Default
import org.fidata.packer.engine.annotations.ExtraProcessed
import org.fidata.packer.engine.annotations.IgnoreIf
import org.fidata.packer.engine.annotations.PostProcess
import org.fidata.packer.engine.annotations.Staging
import org.fidata.packer.engine.types.InterpolableBoolean
import org.fidata.packer.engine.types.InterpolableFile
import org.fidata.packer.engine.types.base.InterpolableObject
import org.fidata.packer.engine.types.InterpolableString
import org.fidata.packer.engine.types.InterpolableStringArray
import groovy.transform.CompileStatic
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.InputFile
import org.gradle.api.tasks.InputFiles
import org.gradle.api.tasks.Internal
import org.gradle.api.tasks.Optional
import org.gradle.api.tasks.PathSensitive
import org.gradle.api.tasks.PathSensitivity

@AutoImplement
@CompileStatic
abstract class ShellLocal implements InterpolableObject<ShellLocal> {
  @JsonAlias('command')
  @Input
  @Optional
  abstract InterpolableStringArray getInline()

  @Input
  @Default({ '/bin/sh -e' })
  @IgnoreIf({ !inline.interpolated })
  abstract InterpolableString getInlineShebang()

  @ExtraProcessed
  abstract InterpolableStringArray getOnlyOn() // TODO

  /*
   * CAVEAT: We assume script is not trying to get its own filename
   * TODOC
   */
  @Staging
  @IgnoreIf({ !inline.interpolated })
  @PostProcess({ String interpolated -> interpolated.replaceFirst(/\A\./, '') })
  abstract InterpolableString getTempfileExtension()

  @InputFile
  /*
   * CAVEAT: We assume script is not trying to get its own path and filename
   * TODOC
   */
  @PathSensitive(PathSensitivity.NONE)
  @Optional
  abstract InterpolableFile getScript()

  @InputFiles
  /*
   * CAVEAT: We assume scripts are not trying to get their own paths and filenames
   * TODOC
   */
  @PathSensitive(PathSensitivity.NONE)
  // TODO: Preserve order
  @Optional
  abstract InterpolableStringArray getScripts()

  @Input
  @ContextVars([ // TODO
    @ContextVar(key = 'WinRMPassword', value = { '' }),
  ])
  @Optional
  abstract InterpolableStringArray getEnvironmentVars()

  @Input
  @Default({ ['/bin/sh', '-c', '{{.Vars}}', '{{.Script}}'] }) // TODO
  @ContextVars([ // TODO
    @ContextVar(key = 'Vars', value = { '' }),
    @ContextVar(key = 'Script', value = { '' }),
    @ContextVar(key = 'Command', value = { '' }), // Deprecated
    @ContextVar(key = 'WinRMPassword', value = { '' }),
  ])
  abstract InterpolableStringArray getExecuteCommand()

  @Internal
  @Default({ Boolean.FALSE })
  // @IgnoreIf() TODO: not Windows
  abstract InterpolableBoolean getUseLinuxPathing()
}
