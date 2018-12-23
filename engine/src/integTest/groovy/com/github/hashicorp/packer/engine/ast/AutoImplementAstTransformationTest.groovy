package com.github.hashicorp.packer.engine.ast

import static groovy.test.GroovyAssert.shouldFail
import com.google.common.collect.HashBasedTable
import groovy.transform.CompileStatic
import com.google.common.collect.ImmutableList
import com.google.common.collect.ImmutableTable
import com.google.common.collect.Maps
import com.google.common.collect.Table
import java.nio.charset.Charset
import java.util.regex.Pattern
import org.codehaus.groovy.control.CompilerConfiguration
import java.nio.file.Path
import com.google.common.base.Charsets
import com.google.common.io.Resources
import groovy.text.StreamingTemplateEngine
import groovy.text.Template
import org.codehaus.groovy.control.CompilePhase
import org.codehaus.groovy.control.MultipleCompilationErrorsException
import org.codehaus.groovy.control.messages.SyntaxErrorMessage
import java.util.regex.Matcher
import junitparams.JUnitParamsRunner
import junitparams.Parameters
import junitparams.naming.TestCaseName
import org.junit.runner.RunWith
import java.nio.file.Paths
import com.google.common.reflect.ClassPath
import org.junit.Test
import com.google.common.collect.ImmutableMap
import org.apache.commons.lang3.ClassPathUtils
import com.google.common.collect.BiMap
import com.google.common.collect.ImmutableBiMap

@RunWith(JUnitParamsRunner)
@CompileStatic
class AutoImplementAstTransformationTest {
  public static final Charset SOURCE_ENCODING = Charsets.UTF_8

  @Test
  @Parameters
  @TestCaseName('testErroneousSource[{index}]: {0}')
  void testErroneousSource(String testName, String source, List<String> errorMessages) {
    Throwable throwable = shouldFail(MultipleCompilationErrorsException, source)

    assert errorMessages == ((MultipleCompilationErrorsException)throwable).errorCollector.errors.collect {
      ((SyntaxErrorMessage)it).cause.originalMessage
    }
  }

  @Test
  @Parameters
  @TestCaseName('testCompilationSource[{index}]: {0}, parameters = {2}')
  void testCompilationSource(String testName, String source, Boolean parameters) {
    GroovyClassLoader groovyClassLoader = getGroovyClassLoader(parameters)
    groovyClassLoader.parseClass source
  }

  @Test
  @Parameters
  @TestCaseName('testCompilationExpected[{index}]: {0}, parameters = {2}')
  void testCompilationExpected(String testName, String source, Boolean parameters) {
    GroovyClassLoader groovyClassLoader = getGroovyClassLoader(parameters)
    groovyClassLoader.parseClass source
  }

  public static final Pattern DECLARATION_PATTERN = Pattern.compile('^// declaration$', Pattern.MULTILINE)

  private static final Template AST_TEST_TEMPLATE = new StreamingTemplateEngine().createTemplate(Resources.toString(Resources.getResource(this, 'ASTTest.groovy.template'), SOURCE_ENCODING))

  @Test
  @Parameters
  @TestCaseName('testASTMatch[{index}]: {0}, parameters = {3}, compilePhase = {4}')
  void testASTMatch(String testName, String source, URL expectedUrl, Boolean parameters, CompilePhase compilePhase) {
    File sourceWithASTTest = File.createTempFile('sourceWithASTTest', '.groovy')
    sourceWithASTTest.write source.replaceFirst(DECLARATION_PATTERN, AST_TEST_TEMPLATE.make(
      compilePhase: compilePhase.inspect(),
      expectedUrl: expectedUrl.toString().inspect(),
    ).toString()), SOURCE_ENCODING.toString()
    getGroovyClassLoader(parameters).parseClass sourceWithASTTest
  }

  /*
   * CAVEAT:
   * We pass serializationTest as file so that we can debug it.
   * Perfect way is to write resource to temp file and debug from there. But for simple test environment
   * this looks like unnecessary complication
   */
  @Test
  @Parameters
  @TestCaseName('testSerialization[{index}]: {0}, parameters = {3}')
  void testSerialization(String testName, String classSource, File serializationTest, Boolean parameters) {
    GroovyClassLoader groovyClassLoader = getGroovyClassLoader(Boolean.FALSE)
    groovyClassLoader.parseClass classSource
    new GroovyShell(groovyClassLoader).evaluate serializationTest
  }

  private static final List<Boolean> BOOLEANS = ImmutableList.of(Boolean.FALSE, Boolean.TRUE)

  private static final Pattern TESTS_PATH = ~"\\A${ Pattern.quote(ClassPathUtils.toFullyQualifiedPath(this, '')) }(.+)\\z"

  private static final Path ERRONEOUS_DIRNAME = Paths.get('erroneous')
  private static final Path SOURCE_FILENAME = Paths.get('source.groovy')
  private static final Path ERROR_MESSAGES_FILENAME = Paths.get('errorMessages.txt')
  private static final Path VALID_DIRNAME = Paths.get('valid')
  private static final Path EXPECTED_DIRNAME = Paths.get('expected')
  private static final BiMap<Boolean, Path> EXPECTED_FILENAMES = ImmutableBiMap.of(
    Boolean.FALSE, Paths.get('withoutParameters.groovy'),
    Boolean.TRUE, Paths.get('withParameters.groovy')
  )
  private static final Path SERIALIZATION_FILENAME = Paths.get('serialization.groovy')

  private static final Map<String, URL> ERRONEOUS_TESTS
  private static final Map<String, URL> VALID_TESTS
  private static final Table<String, Boolean, URL> EXPECTED_TESTS
  private static final Map<String, URL> SERIALIZATION_TESTS

  static {
    Map<String, URL> erroneousTests = [:]
    Map<String, URL> validTests = [:]
    Table<String, Boolean, URL> expectedTests = HashBasedTable.create()
    Map<String, URL> serializationTests = [:]

    ClassPath.from(this.classLoader).resources.each { ClassPath.ResourceInfo it ->
      Matcher m = it.resourceName =~ TESTS_PATH
      if (m.matches()) {
        Path resourcePath = Paths.get(m.group(1))
        if (resourcePath[0] == ERRONEOUS_DIRNAME && resourcePath.nameCount == 3 && resourcePath[2] == SOURCE_FILENAME) {
          erroneousTests[resourcePath[1].toString()] = it.url()
        } else if (resourcePath[0] == VALID_DIRNAME && resourcePath.nameCount == 3 && resourcePath[2] == SOURCE_FILENAME) {
          String testName = resourcePath[1].toString()
          URL url = it.url()

          validTests[testName] = url

          Path expectedDir = Paths.get(url.toURI()).resolveSibling(EXPECTED_DIRNAME)
          EXPECTED_FILENAMES.each { Boolean parameters, Path filename ->
            expectedTests.put testName, parameters, expectedDir.resolve(filename).toUri().toURL()
          }
        } else if (resourcePath[0] == VALID_DIRNAME && resourcePath.nameCount == 3 && resourcePath[2] == SERIALIZATION_FILENAME) {
          serializationTests[resourcePath[1].toString()] = it.url()
        }
      } else {
        null
      }
    }

    ERRONEOUS_TESTS = ImmutableMap.copyOf(erroneousTests)
    VALID_TESTS = ImmutableMap.copyOf(validTests)
    EXPECTED_TESTS = ImmutableTable.copyOf(expectedTests)
    SERIALIZATION_TESTS = ImmutableMap.copyOf(serializationTests)
  }

  private static Object[] parametersForTestErroneousSource() {
    Object[] result = ERRONEOUS_TESTS.collect { String testName, URL url ->
      [
        testName,
        Resources.toString(url, SOURCE_ENCODING),
        Resources.asCharSource(Paths.get(url.toURI()).resolveSibling(ERROR_MESSAGES_FILENAME).toUri().toURL(), SOURCE_ENCODING).readLines()
      ].toArray(new Object[3])
    }.toArray()
    assert result.length > 0
    result
  }

  private static Object[] parametersForTestCompilationSource() {
    Object[] result = GroovyCollections.combinations([
      VALID_TESTS.collect { String testName, URL url ->
        [
          testName,
          Resources.toString(url, SOURCE_ENCODING)
        ]
      },
      BOOLEANS
    ]).collect { ((Iterable)it).flatten().toArray(new Object[3]) }.toArray()
    assert result.length > 0
    result
  }

  private static Object[] parametersForTestCompilationExpected() {
    Object[] result = EXPECTED_TESTS.cellSet().collect { Table.Cell<String, Boolean, URL> cell ->
      [
        cell.rowKey,
        Resources.toString(cell.value, SOURCE_ENCODING),
        cell.columnKey
      ].toArray(new Object[3])
    }.toArray()
    assert result.length > 0
    result
  }

  private static Object[] parametersForTestASTMatch() {
    Object[] result = GroovyCollections.combinations([
      EXPECTED_TESTS.cellSet().collect { Table.Cell<String, Boolean, URL> cell ->
        [
          cell.rowKey,
          Resources.toString(VALID_TESTS[cell.rowKey], SOURCE_ENCODING),
          cell.value,
          cell.columnKey
        ]
      },
      [
        CompilePhase.SEMANTIC_ANALYSIS,
        /*
         * Other compiler phases don't work, and so are not tested.
         * After CANONICALIZATION phase expected AST has invoke, get and set methods
         */
      ]
    ]).collect { ((Iterable)it).flatten().toArray(new Object[5]) }.toArray()
    assert result.length > 0
    result
  }

  private static Object[] parametersForTestSerialization() {
    Object[] result = GroovyCollections.combinations([
      SERIALIZATION_TESTS.collect { String testName, URL url ->
        [
          testName,
          Resources.toString(Paths.get(url.toURI()).resolveSibling(SOURCE_FILENAME).toUri().toURL(), SOURCE_ENCODING),
          new File(url.toURI())
        ]
      },
      BOOLEANS,
    ]).collect { ((Iterable)it).flatten().toArray(new Object[4]) }.toArray()
    assert result.length > 0
    result
  }

  private static final Map<Boolean, CompilerConfiguration> COMPILER_CONFIGURATIONS = ImmutableMap.copyOf(
    BOOLEANS.collect { Boolean parameters ->
      CompilerConfiguration compilerConfiguration = new CompilerConfiguration(/* TODO: Is there a way to get compiler configuration used to compile this test ? */)
      compilerConfiguration.sourceEncoding = SOURCE_ENCODING.name()
      compilerConfiguration.parameters = parameters
      compilerConfiguration.debug = true
      Maps.immutableEntry(parameters, compilerConfiguration)
    }
  )

  private static GroovyClassLoader getGroovyClassLoader(Boolean parameters) {
    new GroovyClassLoader(this.classLoader, COMPILER_CONFIGURATIONS[parameters])
  }
}
