package com.github.hashicorp.packer.template

import com.fasterxml.uuid.Generators
import com.fasterxml.uuid.NoArgGenerator
import com.samskivert.mustache.Mustache
import groovy.transform.CompileStatic
import groovy.transform.EqualsAndHashCode
import org.gradle.api.Task

@EqualsAndHashCode(excludes = ['buildName'])
@CompileStatic
// REVIEWED
final class Context {
  final Map<String, String> userVariables

  final Map<String, String> env

  static final String BUILD_NAME_VARIABLE_NAME = 'BuildName'

  String getBuildName() {
    templateVariables?.get(BUILD_NAME_VARIABLE_NAME)
  }

  final Map<String, ? extends Serializable> templateVariables

  final File templateFile

  // final Task task TODO: This should not be necessary

  final File cwd

  @SuppressWarnings(['NoJavaUtilDate', 'UnnecessaryCast']) // TODO
  private Context(Map<String, String> userVariables, Map<String, String> env, Map<String, ? extends Serializable> templateVariables, File templateFile, File cwd/*, Task task*/) {
    this.userVariables = userVariables?.asImmutable()
    this.env = env?.asImmutable()
    this.templateVariables = templateVariables?.asImmutable()
    this.templateFile = templateFile
    this.cwd = cwd
    // this.task = task

    Map<String, Serializable> aContextTemplateData = (Map<String, Serializable>)[
      'pwd': new File('.').canonicalPath,
      'template_dir': templateFile.parentFile.absolutePath,
      'timestamp': new Date().time.intdiv(1000),
      'uuid': UUID_GENERATOR.generate().toString(), // TODO: We can generate uuid for specific builds only, not for general template
    ]
    if (userVariables) {
      aContextTemplateData.putAll((Map<String, Serializable>)userVariables.collectEntries { Map.Entry<String, String> entry -> ["user `$entry.key`", entry.value] })
    }
    if (env) {
      aContextTemplateData.putAll((Map<String, Serializable>)env.collectEntries { Map.Entry<String, String> entry -> ["env `$entry.key`", entry.value] })
    }
    if (templateVariables) {
      aContextTemplateData.putAll((Map<String, Serializable>)templateVariables.collectEntries { Map.Entry<String, ? extends Serializable> entry -> [".$entry.key", entry.value] })
    }
    // TODO: Make sure all items are immutable / thread-safe
    contextTemplateData = aContextTemplateData.asImmutable()
  }

  Context(Map<String, String> userVariables, Map<String, String> env, File templateFile, File cwd/*, Task task*/) {
    this(userVariables, env, null, templateFile, cwd/*, task*/)
  }

  /**
   * Clones this instance adding specified template variables
   * @param variables Template variables to add
   * @return Clone of this context with added variables
   */
  Context addTemplateVariables(Map<String, ? extends Serializable> variables) {
    Map<String, ? extends Serializable> templateVariables = (Map<String, ? extends Serializable>)[:]
    if (this.templateVariables) {
      templateVariables.putAll this.templateVariables
    }
    templateVariables.putAll variables
    new Context((Map<String, String>)userVariables?.clone(), (Map<String, String>)env?.clone(), templateVariables, templateFile, cwd/*, task*/)
  }

  private final Map<String, Serializable> contextTemplateData

  String interpolateString(String value) {
    /*
     * WORKAROUND:
     * Packer uses Go text/template library. There is no port of it to Java/Groovy
     * This code uses Mustache to parse templates.
     * There could be errors due to slightly different syntax.
     * However, that most probably won't happen in simple templates.
     * <grv87 2018-08-19>
     */
    // TODO: Make sure contextTemplateData is immutable / thread-safe
    mustacheCompiler.compile(value).execute(contextTemplateData)
  }

  File /* TODO: RegularFile ? */ interpolateFile(String value) {
    cwd.toPath().resolve(interpolateString(value)).toFile()
    // Paths.get()
  }

  static private final NoArgGenerator UUID_GENERATOR = Generators.timeBasedGenerator()

  static private final Mustache.Compiler mustacheCompiler = Mustache.compiler()
}
