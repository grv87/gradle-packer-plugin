package com.github.hashicorp.packer.template

import static org.apache.commons.io.FilenameUtils.separatorsToSystem
import static org.apache.commons.io.FilenameUtils.separatorsToUnix
import static org.apache.commons.lang3.SystemUtils.IS_OS_WINDOWS
import static org.fidata.utils.CollectionUtils.flattenValue
import java.nio.file.Paths
import com.google.common.collect.ImmutableMap
import com.google.common.collect.ImmutableList
import javax.annotation.concurrent.Immutable
import java.util.regex.Matcher
import java.util.regex.Pattern
import com.fasterxml.uuid.Generators
import com.fasterxml.uuid.NoArgGenerator
import org.fidata.packer.engine.types.InterpolableString
import com.samskivert.mustache.Mustache
import groovy.transform.CompileStatic
import groovy.transform.EqualsAndHashCode
import org.gradle.api.file.ConfigurableFileTree
import java.nio.file.Path
import java.time.Instant
import com.samskivert.mustache.Template as MustacheTemplate

/**
 * Interpolation context.
 * This class is immutable except interpolationContext private field which is lazily initialized and thread-safe
 */
// equals is used in InterpolableObject to check whether an object is already interpolated
// TODO: maybe this is not required at all, if we just throw exception whenever object is already interpolated.
// This would be cheaper + give us a possibility to catch some errors
@EqualsAndHashCode(includeFields = true, includes = ['userVariablesValues', 'env', 'templateVariables', 'templateFile', 'cwd'], cache = true)
/* NOT NEEDED
//
// @ImmutableBase // TODO: Groovy 2.5.0 ?
*/
@Immutable // Note: Context is itself is immutable, but templateVariables are mutable
// Maybe we should make other objects mutable too - makes no sense to don't support mutability
@CompileStatic
final class Context {
  private final Map<String, ?> userVariablesValues

  private final Map<String, ?> env

  private final Map<String, ?> templateVariables

  private final File templateFile

  private final Path cwd

  static final String BUILD_NAME_VARIABLE_NAME = 'BuildName'

  String getBuildName() {
    templateVariables[BUILD_NAME_VARIABLE_NAME]
  }

  String getTemplateName() {
    userVariablesValues['name'] ?: templateFile.toPath().fileName.toString()
  }

  // cwd should be already resolved relatively to project dir
  @SuppressWarnings('UnnecessaryCast') // TODO
  private Context(Map<String, ?> userVariablesValues, Map<String, ?> env, Map<String, ?> templateVariables, File templateFile, Path cwd) {
    this.userVariablesValues = userVariablesValues ? ImmutableMap.copyOf(userVariablesValues) : null
    this.env = env ? ImmutableMap.copyOf(env).withDefault { '' } : null // ADDTEST
    this.templateVariables = templateVariables ? ImmutableMap.copyOf(templateVariables) : (Map<String, ?>)[:]
    this.templateFile = templateFile
    this.cwd = cwd
  }

  /**
   * Creates new context
   */
  Context(Map<String, ?> userVariablesValues, Map<String, ?> env, File templateFile, Path cwd) {
    this(
      userVariablesValues,
      env,
      null,
      templateFile,
      cwd
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
      (Map<String, ?>)userVariables.collectEntries { String key, InterpolableString value ->
        [key, userVariablesValues.getOrDefault(key, value.interpolated)]
      },
      null,
      templateFile,
      cwd
    )
  }

  /**
   * Clones this instance adding specified template variables.
   * It is used in stage 3 whenever new variables become available
   * @param variables Template variables to add
   * @return Clone of this context with added variables
   */
  Context withTemplateVariables(Map<String, ?> templateVariables) {
    new Context(
      userVariablesValues,
      env,
      (this.templateVariables + templateVariables),
      templateFile,
      cwd
    )
  }

  static private final Mustache.Compiler MUSCTACHE_COMPILER = Mustache.compiler()

  static Object compileTemplate(String template) {
    MUSCTACHE_COMPILER.compile template
  }

  String interpolateString(Object compiledTemplate) {
    /*
     * WORKAROUND:
     * Packer uses Go text/template library. There is no port of it to Java/Groovy
     * This code uses Mustache to parse templates.
     * There could be errors due to slightly different syntax.
     * However, that most probably won't happen in simple templates.
     * <grv87 2018-08-19>
     */
    ((MustacheTemplate)compiledTemplate).execute interpolationContext
  }

  Path interpolatePath(String value) {
    resolvePath(interpolateString(value))
    // Paths.get()
  }

  /*
   * TOTHINK: maybe these should be private. But they are convenient for File provisioner.
   * Also, if we wouldn't operate with Path at all, we could use instance of Directory as cwd
   */
  // Result of this is always absolute
  /* TOTHINK private*/ Path resolvePath(Path path) {
    cwd.resolve(path)
  }

  // Result of this is always absolute
  /* TOTHINK private*/ Path resolvePath(String path) {
    cwd.resolve(path)
  }

  File resolveFile(Path path) {
    resolvePath(path).toFile()
  }

  File /* TODO */ resolveDirectory(String path) {

   // TODO project.layout.projectDirectory.dir(resolvePath(path).toString())
  }

  Iterator<File> resolveFiles(String... paths) {

    // TODO project.files paths.collect { String path -> resolvePath(path) }
  }

  Iterator<File> resolveFileTree(String path, @DelegatesTo(ConfigurableFileTree) Closure closure) { // TODO: @ClosureParams

    // TODO project.fileTree resolvePath(path), closure
  }

  private static final String ABS_PREFIX = IS_OS_WINDOWS ? '/' : ''

  // DownloadableURL processes a URL that may also be a file path and returns
  // a completely valid URL representing the requested file. For example,
  // the original URL might be "local/file.iso" which isn't a valid URL,
  // and so DownloadableURL will return "file://local/file.iso"
  // No other transformations are done to the path.
  // TODO: @throws
  URI resolveUri(String original) {
    // Code from packer/common DownloadableURL
    String result

    // Check that the user specified a UNC path, and promote it to an smb:// uri.
    if (original.startsWith('\\\\') && original.length() > 2 && original[2] != '?') {
      result = separatorsToUnix(original[2..-1])
      return /*project.uri*/new URI("smb://$result")
    }

    // Fix the url if it's using bad characters commonly mistaken with a path.
    original = separatorsToUnix(original)

    try {
      // Check to see that this is a parseable URL with a scheme and a host.
      // If so, then just pass it through.
      URI u = new URI(original)
      if (u.scheme && u.host) {
        return u
      }
      // If it's a file scheme, then convert it back to a regular path so the next
      // case which forces it to an absolute path, will correct it.
      if (u.scheme?.toLowerCase() == 'file') {
        original = u.path
      }
    } catch (URISyntaxException ignored) {
      // This means that it is not an URI
      // Then we assume it is a regular path
    }

    // If we're on Windows and we start with a slash, then this absolute path
    // is wrong. Fix it up, so the next case can figure out the absolute path.
    String[] rpath = original.split('/', 2)
    if (rpath[0].empty && IS_OS_WINDOWS) {
      result = rpath[1]
    } else {
      result = original
    }

    // Since we should be some kind of path (relative or absolute), check
    // that the file exists, then make it an absolute path so we can return an
    // absolute uri.
    if (cwd.resolve(result).toFile().exists()) {
      Path resultPath = cwd.resolve(separatorsToSystem(result)).toAbsolutePath()

      resultPath = resultPath.toRealPath()

      resultPath = resultPath.normalize()

      return new URI("file://$ABS_PREFIX${ separatorsToUnix(resultPath.toString()) }")
    }

    // Otherwise, check if it was originally an absolute path, and fix it if so.
    if (original.startsWith('/')) {
      return new URI("file://$ABS_PREFIX$result")
    }

    // Anything left should be a non-existent relative path. So fix it up here.
    return new URI("file://./${ separatorsToUnix(Paths.get(result).normalize().toString()) }")
  }

  private final class InterpolationContext implements Map<String, Serializable> /* TODO: implements Mustache.CustomContext */ {
    @Override
    int size() {
      (int)parameterizedFunctions*.value*.size().sum() + parameterlessConstantFunctions.size()
    }

    @Override
    boolean isEmpty() {
      return false
    }

    @Override
    boolean containsKey(Object key) {
      String stringKey = (String)key
      parameterizedFunctions.each { Pattern pattern, Map<String, ?> values ->
        Matcher matcher = stringKey =~ pattern
        if (matcher.matches()) {
          return values.containsKey(stringKey)
        }
      }
      return parameterlessConstantFunctions.containsKey(stringKey)
    }

    @Override
    boolean containsValue(Object value) {
      parameterizedFunctions.values().any { Map<String, ?> values ->
        values.containsValue(value)
      } || parameterlessConstantFunctions.containsValue(value)
    }

    // TODO: Find usable Gradle built-in/third-party library method
    @Override
    final Serializable get(Object key) {
      String stringKey = (String)key
      parameterizedFunctions.each { Pattern pattern, Map<String, ?> values ->
        Matcher matcher = stringKey =~ pattern
        if (matcher.matches()) {
          return flattenValue(values[matcher.group(1)]) // TOTEST
        }
      }
      return parameterlessConstantFunctions[stringKey]
    }

    @Override
    Serializable put(String key, Serializable value) {
      throw new UnsupportedOperationException()
    }

    @Override
    Serializable remove(Object key) {
      throw new UnsupportedOperationException()
    }

    @Override
    void putAll(Map<? extends String, ? extends Serializable> m) {
      throw new UnsupportedOperationException()
    }

    @Override
    void clear() {
      throw new UnsupportedOperationException()
    }

    @Override
    Set<String> keySet() {
      throw new UnsupportedOperationException()
    }

    @Override
    Collection<Serializable> values() {
      ImmutableList.copyOf((Collection<Serializable>)((parameterizedFunctions.values().collectMany { Map<String, ?> value ->
        value.values().collect { Object subvalue -> flattenValue(subvalue) }
      } + parameterlessConstantFunctions.values())))
    }

    @Override
    Set<Entry<String, Serializable>> entrySet() {
      throw new UnsupportedOperationException()
    }

    private final Map<Pattern, Map<String, ?>> parameterizedFunctions = ImmutableMap.copyOf(/*(Map<Pattern, Map<String, ?>>)*/[
      (~/\Aenv\s+`(\S+)`\z/): env,
      (~/\Auser\s+`(\S+)`\z/): userVariablesValues,
      (~/\A\.(\S+)\z/): templateVariables,
    ])

    // TODO: mark string as mutable if timestamp or uuid is used
    private final Map<String, Serializable> parameterlessConstantFunctions = ImmutableMap.copyOf((Map<String, Serializable>)[
      'pwd': new File('.').canonicalPath, // TODO ???
      'template_dir': templateFile.parentFile.absolutePath,
      'timestamp': Instant.now().epochSecond,
      'uuid': UUID_GENERATOR.generate()/*.toString()*/,
    ])

    static private final NoArgGenerator UUID_GENERATOR = Generators.timeBasedGenerator()
  }


  // This should be initialized exactly once, otherwise different threads could get different timestamp or uuid
  @Lazy
  private volatile InterpolationContext interpolationContext = new InterpolationContext()
}
