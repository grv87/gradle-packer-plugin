package com.github.hashicorp.packer.template

import com.google.common.collect.ImmutableCollection
import com.google.common.collect.ImmutableSet
import groovy.transform.CompileDynamic

import java.util.function.BiConsumer

import static org.apache.commons.io.FilenameUtils.separatorsToUnix
import com.google.common.collect.ImmutableMap
import javax.annotation.concurrent.Immutable
import java.util.regex.Matcher
import java.util.regex.Pattern
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
    this.env = env ? ImmutableMap.copyOf(env).withDefault { '' } : null // ADDTEST
    this.templateVariables = templateVariables ? ImmutableMap.copyOf(templateVariables) : (Map<String, ? extends Serializable>)[:]
    this.templateFile = templateFile
    this.cwd = cwd
    this.project = project
  }

  /**
   * Creates new context
   */
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

  /**
   * Gets context for stage 1
   * @return
   */
  Context getForVariables() {
    new Context(
      null,
      env,
      templateFile,
      cwd
    )
  }

  /**
   * Gets context for stage 2
   * @return
   */
  Context forTemplateBody(Map<String, InterpolableString> userVariables) { // TODO: we could interpolate variables right here
    new Context(
      (Map<String, String>)userVariables.collectEntries { Map.Entry<String, InterpolableString> entry ->
        [entry.key, userVariablesValues.getOrDefault(entry.key, entry.value.interpolatedValue)]
      },
      null,
      templateFile,
      cwd
    )
  }

  /**
   * Gets context for stage 3
   * @return
   */
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
   * Clones this instance adding specified template variables.
   * It is used in stage 3 whenever new variables become available
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
    mustacheCompiler.compile(value).execute(interpolationContext)
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

  /*
   * WORKAROUND:
   * Groovy bug https://issues.apache.org/jira/browse/GROOVY-7985.
   * Nested generics are not supported in static compile mode.
   * Fixed in Groovy 2.5.0-rc-3
   * <grv87 2018-11-10>
   */
  @CompileDynamic
  class InterpolationContext extends ImmutableMap<String, String> /* TODO implements Mustache.CustomContext */ {
    @Override
    int size() {
      parameterizedFunctions*.value*.size().sum() + parameterlessFunctions.size()
    }

    @Override
    final String get(/* TODO: not allowed on PARAMETER ?? @Nullable */ Object key) {
      String stringKey = (String)key
      parameterizedFunctions.each { Pattern pattern, Map<String, ? extends Serializable> values ->
        Matcher matcher = stringKey =~ pattern
        if (matcher.matches()) {
          return values[matcher.group(1)] // TOTEST
        }
      }
      return parameterlessFunctions[stringKey]
    }

    /*@Override
    void forEach(BiConsumer<? super String, ? super String> action) {
      throw new UnsupportedOperationException()
    }*/

    private final Map<Pattern, Map<String, ? extends Serializable>> parameterizedFunctions = copyOf(/*(Map<Pattern, Map<String, ? extends Serializable>>)*/[
      (~/^env\s+`(\S+)`$/): env,
      (~/^user\s+`(\S+)`$/): userVariablesValues,
      (~/^\.(\S+)$/): templateVariables,
    ])

    // TODO: mark string as mutable if timestamp or uuid is used
    private final Map<String, Serializable> parameterlessFunctions = copyOf((Map<String, Serializable>)[
      'pwd': new File('.').canonicalPath, // TODO ???
      'template_dir': templateFile.parentFile.absolutePath,
      'timestamp': Instant.now().epochSecond,
      'uuid': UUID_GENERATOR.generate()/*.toString()*/,
    ])

    protected ImmutableSet<Entry<String, String>> createEntrySet() {
      throw new UnsupportedOperationException() // TODO
    }

    protected ImmutableSet<String> createKeySet() {
      // parameterizedFunctions*.value*.keySet().flatten().toSet() + parameterlessFunctions.keySet()
      throw new UnsupportedOperationException() // TODO ?
    }

    protected ImmutableCollection<String> createValues() {
      throw new UnsupportedOperationException() // TODO
    }

    boolean isPartialView() {
      false
    }
  }

  @Lazy
  private volatile InterpolationContext interpolationContext = { new InterpolationContext() }()
}
