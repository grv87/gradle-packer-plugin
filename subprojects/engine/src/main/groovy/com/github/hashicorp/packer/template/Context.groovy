package com.github.hashicorp.packer.template

import com.fasterxml.uuid.Generators
import com.fasterxml.uuid.NoArgGenerator
import com.samskivert.mustache.Mustache
import groovy.transform.CompileStatic
import groovy.transform.EqualsAndHashCode
import org.gradle.api.Project
import org.gradle.api.file.ConfigurableFileTree
import org.gradle.api.file.Directory
import org.gradle.api.file.FileCollection
import org.gradle.api.file.FileTree
import org.gradle.api.file.RegularFile

import java.nio.file.Path
import java.time.Instant

// equals is used in InterpolableObject to check whether
// TODO: maybe this is not required at all, if we just throw exception whenever object is already interpolated.
// This would be cheaper + give us a possibility to catch some errors
@EqualsAndHashCode(excludes = ['buildName'])
/* NOT NEEDED
//
// @ImmutableBase // TODO: Groovy 2.5.0
*/
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

  final Path cwd

  final Project project

  @SuppressWarnings('UnnecessaryCast') // TODO
  private Context(Map<String, String> userVariables, Map<String, String> env, Map<String, ? extends Serializable> templateVariables, File templateFile, Path cwd, Project project) {
    this.userVariables = userVariables?.asImmutable()
    this.env = env?.asImmutable()
    this.templateVariables = templateVariables?.asImmutable()
    this.templateFile = templateFile
    this.cwd = project.file(cwd.toFile()).absoluteFile.toPath() // cwd is absolute, so all resolutions
    this.project = project

    // TODO: mark string as mutable if timestamp or uuid is used
    Map<String, Serializable> aContextTemplateData = (Map<String, Serializable>)[
      'pwd': new File('.').canonicalPath,
      'template_dir': templateFile.parentFile.absolutePath,
      'timestamp': Instant.now().epochSecond,
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

  Context(Map<String, String> userVariables, Map<String, String> env, File templateFile, Path cwd, Project project) {
    this(userVariables, env, null, templateFile, cwd, project)
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
    new Context((Map<String, String>)userVariables?.clone(), (Map<String, String>)env?.clone(), templateVariables, templateFile, cwd, project)
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

  Path /* TODO: RegularFile ? */ interpolatePath(String value) {
    resolvePath(interpolateString(value))
    // Paths.get()
  }

  /*
   * TOTHINK: maybe these should be private. But they are convenient for File provisioner.
   * Also, if we wouldn't operate with Path at all, we could use instance of Directory as cwd
   */
  // Result of this is always absolute
  Path resolvePath(Path path) {
    cwd.resolve(path)
  }

  // Result of this is always absolute
  /* TOTHINK private*/ Path resolvePath(String path) {
    cwd.resolve(path)
  }

  RegularFile resolveRegularFile(Path path) {
    project.layout.projectDirectory.file(resolvePath(path).toString())
  }

  Directory resolveDirectory(Path path) {
    project.layout.projectDirectory.dir(resolvePath(path).toString())
  }

  FileCollection resolveFiles(Path... paths) {
    project.files paths.collect { Path path -> resolvePath(path) }
  }

  FileTree resolveFileTree(Path path, @DelegatesTo(ConfigurableFileTree) Closure closure) {
    project.fileTree resolvePath(path), closure
  }

  static private final NoArgGenerator UUID_GENERATOR = Generators.timeBasedGenerator()

  static private final Mustache.Compiler mustacheCompiler = Mustache.compiler()
}
