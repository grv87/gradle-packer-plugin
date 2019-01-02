/*
 * AbstractPackerBuild class
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
 */
package org.fidata.gradle.packer.tasks

import com.github.hashicorp.packer.packer.Artifact
import com.google.common.collect.ImmutableList
import org.fidata.gradle.packer.PackerEnginePlugin
import org.fidata.packer.engine.AbstractEngine
import com.github.hashicorp.packer.template.OnlyExcept
import org.fidata.packer.engine.TemplateBuildResult
import org.fidata.packer.engine.annotations.ExtraProcessed
import org.gradle.api.file.RegularFile
import org.gradle.api.logging.configuration.ConsoleOutput
import org.gradle.api.provider.Property
import org.fidata.gradle.packer.tasks.arguments.PackerOnlyExceptReadOnlyArgument
import org.fidata.gradle.packer.tasks.arguments.PackerTemplateReadOnlyArgument
import org.gradle.api.provider.Provider
import com.github.hashicorp.packer.template.Builder
import com.github.hashicorp.packer.template.Context
import com.github.hashicorp.packer.enums.OnError
import org.fidata.gradle.packer.tasks.arguments.PackerMachineReadableArgument
import org.gradle.api.logging.LogLevel
import org.fidata.gradle.packer.PackerExecSpec
import org.fidata.gradle.packer.tasks.arguments.PackerVarArgument
import groovy.transform.CompileStatic
import com.github.hashicorp.packer.template.Template
import org.gradle.api.tasks.Internal
import org.gradle.api.tasks.Nested
import org.gradle.api.tasks.Console

import java.util.function.Supplier

@CompileStatic
abstract class AbstractPackerBuild extends PackerWrapperTask implements PackerMachineReadableArgument, PackerOnlyExceptReadOnlyArgument, PackerVarArgument, PackerTemplateReadOnlyArgument {
  @Console
  final Property<Boolean> color

  @Internal
  final Property<Boolean> parallel

  @Internal
  final Property<OnError> onError

  @Internal
  final Property<Boolean> force

  @Console
  final Property<Boolean> debug

  // @Inject
  protected AbstractPackerBuild(/*TODO ProviderFactory providerFactory*/ Provider<RegularFile> templateFile, OnlyExcept onlyExcept = null) {
    PackerTemplateReadOnlyArgument.super.templateFile = templateFile

    PackerOnlyExceptReadOnlyArgument.super.onlyExcept = onlyExcept

    color = project.objects.property(Boolean)
    color.convention project.provider {
      switch (project.gradle.startParameter.consoleOutput) {
        case ConsoleOutput.Plain:
          false
          break
        case ConsoleOutput.Rich:
        case ConsoleOutput.Verbose:
          true
          break
        default:
          // TODO: project.logger.warn
          true
      }
    }

    parallel = project.objects.property(Boolean)
    parallel.convention Boolean.TRUE

    onError = project.objects.property(OnError)
    onError.convention OnError.CLEANUP

    force = project.objects.property(Boolean)
    force.convention project.gradle.startParameter.rerunTasks

    debug = project.objects.property(Boolean)
    debug.convention project.provider { (project.logging.level ?: project.gradle.startParameter.logLevel) <= LogLevel.DEBUG }

    outputs.upToDateWhen {
      cachedBuildResults.every() { TemplateBuildResult templateBuildResult ->
        !templateBuildResult.upToDateWhen || templateBuildResult.upToDateWhen.every { Supplier<Boolean> upToDateWhenProvider ->
          upToDateWhenProvider.get()
        }
      }
    }
  }

  // TOTEST: @Internal
  @Override
  @SuppressWarnings('UnnecessaryGetter') // TODO
  List<String> getCmdArgs() {
    List<String> cmdArgs = PackerTemplateReadOnlyArgument.super.getCmdArgs()

    // Template should be the last, so we insert in the start
    if (color.get() == Boolean.FALSE) {
      cmdArgs.add 0, '-color=false'
    }

    if (parallel.get() == Boolean.FALSE) {
      cmdArgs.add 0, '-parallel=false'
    }

    if (onError.present) {
      /*if (onError == OnError.ASK &&  {
        TODO: ASK will work in interactive mode only
      }*/
      cmdArgs.add 0, "-on-error=$onError"
    }

    if (force.get() == Boolean.TRUE) {
      cmdArgs.add 0, '-force'
    }

    if (debug.get() == Boolean.TRUE) {
      cmdArgs.add 0, '-debug'
    }

    cmdArgs
  }

  @Override
  protected PackerExecSpec configureExecSpec(PackerExecSpec execSpec) {
    PackerExecSpec result = super.configureExecSpec(execSpec)
    result.command 'build'
    result
  }

  @ExtraProcessed
  abstract Template getTemplate()

  /**
   * We cache interpolated templates to overcome the fact
   * that provider can call the closure several times
   *
   * See:
   * https://github.com/gradle/gradle/issues/6787
   * https://github.com/gradle/gradle/issues/4292
   * https://github.com/gradle/gradle/issues/5301
   * <grv87 2019-01-02>
   */
  @Lazy
  private List<Template> cachedInterpolatedTemplates = {
    Template interpolatedTemplate = template.interpolate(new Context(variables.get(), environment, templateFile.get().asFile, workingDir.get().asFile.toPath())) // MARK1

    List<Template> result = new ArrayList<>(onlyExcept.sizeAfterSkip(interpolatedTemplate.builders.size()))
    for (Builder builder in interpolatedTemplate.builders) {
      String buildName = builder.header.buildName
      if (!onlyExcept.skip(buildName)) {
        result.add interpolatedTemplate.interpolateForBuilder(engine, buildName, project)
      }
    }
    result
  }()

  @Lazy
  private List<TemplateBuildResult> cachedBuildResults = {
    ImmutableList.copyOf(cachedInterpolatedTemplates.collect { Template template ->
      template.build()
    })
  }()

  @Lazy
  private List<Artifact> cachedArtifacts = {
    (List<Artifact>)cachedBuildResults*.artifacts.flatten()
  }()

  @Nested
  final Provider<List<Template>> interpolatedTemplates = project.provider { cachedInterpolatedTemplates }

  @Nested
  final Provider<List<Artifact>> artifacts = project.provider { cachedArtifacts }

  protected final AbstractEngine<Template> getEngine() {
    project.gradle.plugins.getPlugin(PackerEnginePlugin).engine
  }
}
