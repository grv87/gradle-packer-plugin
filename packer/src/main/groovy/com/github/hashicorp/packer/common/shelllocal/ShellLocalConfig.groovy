/*
 * ShellLocalConfig class
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
 * file common/shell-local/config.go
 * under the terms of the Mozilla Public License, v. 2.0.
 */
package com.github.hashicorp.packer.common.shelllocal

import com.fasterxml.jackson.annotation.JsonAlias
import com.google.common.annotations.Beta
import go.runtime.GOOS
import org.fidata.gradle.utils.OrderedInputFilesWrapper
import org.fidata.packer.engine.annotations.AutoImplement
import org.fidata.packer.engine.annotations.ComputedExtraProcessed
import org.fidata.packer.engine.annotations.ComputedNested
import org.fidata.packer.engine.annotations.ContextVar
import org.fidata.packer.engine.annotations.ContextVars
import org.fidata.packer.engine.annotations.Default
import org.fidata.packer.engine.annotations.ExtraProcessed
import org.fidata.packer.engine.annotations.OnlyIf
import org.fidata.packer.engine.annotations.PostProcess
import org.fidata.packer.engine.annotations.Staging
import org.fidata.packer.engine.types.InterpolableBoolean
import org.fidata.packer.engine.types.InterpolableFile
import org.fidata.packer.engine.types.InterpolableGOOS
import org.fidata.packer.engine.types.base.InterpolableObject
import org.fidata.packer.engine.types.InterpolableString
import org.fidata.packer.engine.types.InterpolableStringArray
import groovy.transform.CompileStatic
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.InputFile
import org.gradle.api.tasks.Internal
import org.gradle.api.tasks.Optional
import org.gradle.api.tasks.PathSensitive
import org.gradle.api.tasks.PathSensitivity

/**
 * Common configuration for {@code shell-local} provisioner and post-processor
 */
@AutoImplement
@CompileStatic
abstract class ShellLocalConfig implements InterpolableObject<ShellLocalConfig> {
  /**
   * This is an array of commands to execute. The
   * commands are concatenated by newlines and turned into a single file, so
   * they are all executed within the same context. This allows you to change
   * directories in one command and use something in the directory in the next
   * and so on. Inline scripts are the easiest way to pull off simple tasks
   * within the machine.
   *
   * If you are building a windows vm on AWS, Azure or Google Compute and would
   * like to access the generated password that Packer uses to connect to the
   * instance via WinRM, you can use the template variable {@code {{.WinRMPassword}}}
   * to set this as an environment variable.
   *
   * Packer also allows to specify a single command to execute
   * with {@code command} configuration option.
   * This option is deprecated, and, if found in source JSON template,
   * it will be deserialized here instead.
   *
   * @return An inline script to execute
   */
  @JsonAlias('command')
  @Input
  @Optional
  @ContextVars([ // TODO
    @ContextVar(key = 'WinRMPassword', value = { '' }),
  ])
  @OnlyIf({ -> checkOnlyOn() })
  abstract InterpolableStringArray getInline()

  /**
   * The
   * <a href="http://en.wikipedia.org/wiki/Shebang_%28Unix%29">shebang</a> value to use
   * when running commands specified by {@code inline}. By default, this is
   * empty on windows and {@code /bin/sh -e} otherwise. If you're not using {@code inline},
   * then this configuration has no
   * effect. <b>Important:</b> If you customize this, be sure to include something
   * like the {@code -e} flag, otherwise individual steps failing won't fail the
   * provisioner/post-processor.
   *
   * @return The shebang value used when running inline scripts
   */
  @Input
  @Default({ GOOS.CURRENT == GOOS.WINDOWS ? null : '/bin/sh -e' })
  @OnlyIf({ -> inline.interpolated })
  @OnlyIf({ -> checkOnlyOn() })
  abstract InterpolableString getInlineShebang()

  /**
   * This is an array of <a href="https://golang.org/doc/install/source#environment">runtime operating
   * systems</a> where
   * {@code shell-local} will execute. This allows you to execute {@code shell-local} <i>only</i>
   * on specific operating systems. By default, shell-local will always run if
   * {@code only_on} is not set.
   *
   * @return An array of multiple Runtime OSs to run on
   */
  @ExtraProcessed
  @groovy.transform.Internal
  abstract List<InterpolableGOOS> getOnlyOn() // TODO

  /**
   * Checks whether current OS meet {@code onlyOn} requirement.
   *
   * @return True if {@code onlyOn} list is empty, or current OS is in the list
   */
  @ComputedExtraProcessed
  private final Boolean checkOnlyOn() {
    !onlyOn ||
    onlyOn.any { InterpolableGOOS interpolableGOOS ->
      interpolableGOOS.interpolated == GOOS.CURRENT
    }
  }

  /**
   * The file extension to use for the file generated from the inline commands
   *
   * @return The file extension to use for the file generated from the inline commands
   */
  @Staging
  @Default(value = { -> GOOS.CURRENT == GOOS.WINDOWS ? 'cmd' : '' }, dynamic = true)
  @OnlyIf({ -> inline.interpolated })
  @OnlyIf({ -> checkOnlyOn() })
  @PostProcess({ String interpolated -> interpolated.replaceFirst(/\A\./, '') })
  abstract InterpolableString getTempfileExtension()

  /**
   * The path to a script to execute. This path can be
   * absolute or relative. If it is relative, it is relative to the working
   * directory when Packer is executed.
   *
   * @return The local path of the shell script to upload and execute
   */
  @InputFile
  @PathSensitive(PathSensitivity.NONE)
  @Optional
  @OnlyIf({ -> checkOnlyOn() })
  abstract InterpolableFile getScript()

  /**
   * An array of scripts to execute. The scripts
   * will be executed in the order specified. Each script is executed in
   * isolation, so state such as variables from one script won't carry on to the
   * next.
   *
   * @return An array of multiple scripts to run
   */
  @ExtraProcessed
  @PathSensitive(PathSensitivity.NONE)
  // TODO: Preserve order
  @OnlyIf({ -> checkOnlyOn() })
  abstract List<InterpolableFile> getScripts()

  @ComputedNested
  final OrderedInputFilesWrapper getScriptsInOrder() {
    // TOTHINK: cache result ?
    new OrderedInputFilesWrapper(scripts*.interpolated)
  }

  /**
   * An array of key/value pairs to
   * inject prior to the {@code execute_command}. The format should be {@code key=value}.
   * Packer injects some environmental variables by default into the
   * environment, as well, which are covered in the section below. If you are
   * building a windows vm on AWS, Azure or Google Compute and would like to
   * access the generated password that Packer uses to connect to the instance
   * via WinRM, you can use the template variable {@code {{.WinRMPassword}}} to set
   * this as an environment variable. For example:
   * {@code "environment_vars": "WINRMPASS={{.WinRMPassword}}"}
   *
   * @return An array of environment variables that will be injected before
   * your command(s) are executed
   */
  @Input
  @ContextVars([ // TODO
    @ContextVar(key = 'WinRMPassword', value = { '' }),
  ])
  @Optional
  @OnlyIf({ -> checkOnlyOn() })
  abstract InterpolableStringArray getEnvironmentVars()

  /**
   * The command used to execute the
   * script. By default this is {@code ["/bin/sh", "-c", "{{.Vars}}", "{{.Script}}"]}
   * on unix and {@code ["cmd", "/c", "{{.Vars}}", "{{.Script}}"]} on windows. This is
   * treated as a <a href="https://https://www.packer.io/docs/templates/engine.html">template engine</a>. There are two
   * available variables: {@code Script}, which is the path to the script to run, and
   * {@code Vars}, which is the list of {@code environment_vars}, if configured.
   *
   * If you choose to set this option, make sure that the first element in the
   * array is the shell program you want to use (for example, "sh"), and a later
   * element in the array must be {@code {{.Script}}}.
   *
   * This option provides you a great deal of flexibility. You may choose to
   * provide your own shell program, for example "/usr/local/bin/zsh" or even
   * "powershell.exe". However, with great power comes great responsibility -
   * these commands are not officially supported and things like environment
   * variables may not work if you use a different shell than the default.
   *
   * For backwards compatibility, you may also use {{.Command}}, but it is
   * decoded the same way as {{.Script}}. We recommend using {{.Script}} for the
   * sake of clarity, as even when you set only a single `command` to run,
   * Packer writes it to a temporary file and then runs it as a script.
   *
   * For backwards compatibility, Packer accepts a string instead
   * of an array of strings for {@code execute_command} configuration
   * of {@code shell-local} post-processor. This is not supported in this library.
   *
   * If you are building a windows vm on AWS, Azure or Google Compute and would
   * like to access the generated password that Packer uses to connect to the
   * instance via WinRM, you can use the template variable {@code {{.WinRMPassword}}}
   * to set this as an environment variable.
   *
   * @return The command used to execute the script
   */
  @Input
  @Default(value = { ->
    GOOS.CURRENT == GOOS.WINDOWS ?
    ['cmd', '/c', '{{.Vars}}', '{{.Script}}'] :
    ['/bin/sh', '-c', '{{.Vars}}', '{{.Script}}']
  }, dynamic = true) // TODO
  @ContextVars([ // TODO
    @ContextVar(key = 'Vars', value = { environmentVars.interpolated /* TODO */ }),
    @ContextVar(key = 'Script', value = { '' }),
    @ContextVar(key = 'Command', value = { '' }), // Deprecated
    // @ContextVar(key = 'WinRMPassword', value = { '' }), // TOTEST: Bug in Packer documentation ?
  ])
  @OnlyIf({ -> checkOnlyOn() })
  abstract InterpolableStringArray getExecuteCommand()

  /**
   * This is only relevant to windows hosts. If you
   * are running Packer in a Windows environment with the Windows Subsystem for
   * Linux feature enabled, and would like to invoke a bash script rather than
   * invoking a Cmd script, you'll need to set this flag to true; it tells
   * Packer to use the linux subsystem path for your script rather than the
   * Windows path. (e.g. /mnt/c/path/to/your/file instead of
   * C:/path/to/your/file). Please see the example below for more guidance on
   * how to use this feature. If you are not on a Windows host, or you do not
   * intend to use the shell-local provisioner/post-processor to run a bash script, please
   * ignore this option. If you set this flag to true, you still need to provide
   * the standard windows path to the script when providing a `script`. This is
   * a beta feature.
   *
   * @return Whether to use the linux subsystem path rather than the Windows path
   */
  @Internal
  @Default({ Boolean.FALSE })
  @OnlyIf({ -> GOOS.CURRENT == GOOS.WINDOWS })
  @OnlyIf({ -> checkOnlyOn() })
  @Beta
  abstract InterpolableBoolean getUseLinuxPathing()
}
