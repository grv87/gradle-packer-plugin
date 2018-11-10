package com.github.hashicorp.packer.template

import com.google.common.collect.ImmutableMap

import javax.annotation.concurrent.Immutable

import static org.apache.commons.io.FilenameUtils.separatorsToUnix
import com.fasterxml.uuid.Generators
import com.fasterxml.uuid.NoArgGenerator
import com.github.hashicorp.packer.engine.types.InterpolableString
import com.samskivert.mustache.Mustache
import groovy.transform.CompileStatic
import groovy.transform.EqualsAndHashCode
import org.gradle.api.Project
import org.gradle.api.file.ConfigurableFileTree
import org.gradle.api.file.Directory
import org.gradle.api.file.FileCollection
import org.gradle.api.file.FileTree
import java.nio.file.Path
import java.time.Instant

// equals is used in InterpolableObject to check whether
// TODO: maybe this is not required at all, if we just throw exception whenever object is already interpolated.
// This would be cheaper + give us a possibility to catch some errors
@EqualsAndHashCode(excludes = ['buildName'])
/* NOT NEEDED
//
// @ImmutableBase // TODO: Groovy 2.5.0 ?
*/
@Immutable
@CompileStatic
// REVIEWED
final class Context {
  private final Map<String, String> userVariablesValues

  private final Map<String, String> env

  static final String BUILD_NAME_VARIABLE_NAME = 'BuildName'

  String getBuildName() {
    templateVariables.get(BUILD_NAME_VARIABLE_NAME)
  }


  private final Map<String, ? extends Serializable> templateVariables

  private final File templateFile

  private final Path cwd

  private final Project project

  String getTemplateName() {
    userVariablesValues['name'] ?: templateFile.toPath().fileName.toString()
  }

  // cwd should be already resolved relatively to project dir
  @SuppressWarnings('UnnecessaryCast') // TODO
  private Context(Map<String, String> userVariablesValues, Map<String, String> env, Map<String, ? extends Serializable> templateVariables, File templateFile, Path cwd, Project project) {
    this.userVariablesValues = userVariablesValues ? ImmutableMap.copyOf(userVariablesValues) : null
    this.env = env ? ImmutableMap.copyOf(env) : null
    this.templateVariables = templateVariables ? ImmutableMap.copyOf(templateVariables) : (Map<String, ? extends Serializable>)[:]
    this.templateFile = templateFile
    this.cwd = cwd
    this.project = project

    // TODO: mark string as mutable if timestamp or uuid is used
    Map<String, Serializable> aContextTemplateData = (Map<String, Serializable>)[
      'pwd': new File('.').canonicalPath,
      'template_dir': templateFile.parentFile.absolutePath,
      'timestamp': Instant.now().epochSecond,
      'uuid': UUID_GENERATOR.generate().toString(), // TODO: We can generate uuid for specific builds only, not for general template
    ]
    if (userVariablesValues) {
      aContextTemplateData.putAll((Map<String, Serializable>)userVariablesValues.collectEntries { Map.Entry<String, String> entry -> ["user `$entry.key`", entry.value] })
    }
    if (env) {
      aContextTemplateData.putAll((Map<String, Serializable>)env.collectEntries { Map.Entry<String, String> entry -> ["env `$entry.key`", entry.value] })
    }
    if (templateVariables) {
      aContextTemplateData.putAll((Map<String, Serializable>)templateVariables.collectEntries { Map.Entry<String, ? extends Serializable> entry -> [".$entry.key", entry.value] })
    }
    // TODO: Make sure all items are immutable / thread-safe
    contextTemplateData = ImmutableMap.copyOf(aContextTemplateData)
  }

  Context(Map<String, String> userVariablesValues, Map<String, String> env, File templateFile, Path cwd) {
    this(
      userVariablesValues,
      env,
      null,
      templateFile,
      cwd,
      null
    )
  }

  Context getForVariables() {
    new Context(
      null,
      env,
      templateFile,
      cwd
    )
  }

  Context forTemplateBody(Map<String, InterpolableString> userVariables) {
    new Context(
      (Map<String, String>)userVariables.collectEntries { Map.Entry<String, InterpolableString> entry ->
        [entry.key, userVariablesValues.getOrDefault(entry.key, entry.value.interpolatedValue)]
      },
      null,
      templateFile,
      cwd
    )
  }

  Context forProject(Project project) {
    new Context(
      userVariablesValues,
      env,
      templateVariables,
      templateFile,
      cwd,
      project
    )
  }


  /**
   * Clones this instance adding specified template variables
   * @param variables Template variables to add
   * @return Clone of this context with added variables
   */
  Context withTemplateVariables(Map<String, ? extends Serializable> templateVariables) {
    new Context(
      userVariablesValues,
      env,
      (Map<String, ? extends Serializable>)(this.templateVariables + templateVariables),
      templateFile,
      cwd,
      project
    )
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

  Directory resolveDirectory(String path) {
    project.layout.projectDirectory.dir(resolvePath(path).toString())
  }

  FileCollection resolveFiles(String... paths) {
    project.files paths.collect { String path -> resolvePath(path) }
  }

  FileTree resolveFileTree(String path, @DelegatesTo(ConfigurableFileTree) Closure closure) {
    project.fileTree resolvePath(path), closure
  }

  // DownloadableURL processes a URL that may also be a file path and returns
  // a completely valid URL representing the requested file. For example,
  // the original URL might be "local/file.iso" which isn't a valid URL,
  // and so DownloadableURL will return "file://local/file.iso"
  // No other transformations are done to the path.
  URI resolveUri(String original) {
    // Code from packer/common DownloadableURL
    String result

    // Check that the user specified a UNC path, and promote it to an smb:// uri.
    if (original.startsWith('\\\\') && original.length() > 2 && original[2] != '?') {
      result = separatorsToUnix(original[2..-1])
      return /*project.uri*/new URI("smb://$result")
    }

    // Fix the url if it's using bad characters commonly mistaken with a path.
    result = separatorsToUnix(original)

    // Check to see that this is a parseable URL with a scheme and a host.
    // If so, then just pass it through.
    try {
      URI resultUri = new URI(result)
      if (!resultUri.scheme.empty && !resultUri.host.empty) {
        return resultUri
      }
    } catch (URISyntaxException ignored) { }

    // Otherwise we rely on built-in Java algorithms
    return resolvePath(result).toUri()
  }

  static private final NoArgGenerator UUID_GENERATOR = Generators.timeBasedGenerator()

  static private final Mustache.Compiler mustacheCompiler = Mustache.compiler()
}
