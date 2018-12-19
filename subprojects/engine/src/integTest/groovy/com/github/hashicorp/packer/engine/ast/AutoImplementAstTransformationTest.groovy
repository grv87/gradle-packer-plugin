package com.github.hashicorp.packer.engine.ast

import static groovy.test.GroovyAssert.assertScript
import static groovy.test.GroovyAssert.shouldFail
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

@RunWith(JUnitParamsRunner)
// @CompileStatic
class AutoImplementAstTransformationTest {

  public static final Pattern DECLARATION_PATTERN = Pattern.compile('^// declaration$', Pattern.MULTILINE)

  @Test
  @Parameters
  @TestCaseName('testErroneousSource[{index}]: {0}')
  void testErroneousSource(String testName, String source, List<String> errorMessages) {
    Throwable throwable = shouldFail(MultipleCompilationErrorsException, source)

    assert errorMessages == ((MultipleCompilationErrorsException)throwable).errorCollector.errors.collect {
      ((SyntaxErrorMessage)it).cause.originalMessage
    }
  }

  private static CompilerConfiguration getCompilerConfiguration(Boolean parameters) {
    CompilerConfiguration compilerConfiguration = new CompilerConfiguration(/*TODO*/)
    compilerConfiguration.sourceEncoding = Charsets.UTF_8.name()
    compilerConfiguration.parameters = parameters
    compilerConfiguration.debug = true
    compilerConfiguration
  }

  private static final Map<Boolean, CompilerConfiguration> COMPILER_CONFIGURATIONS = ImmutableMap.of(
    Boolean.FALSE, getCompilerConfiguration(Boolean.FALSE),
    Boolean.TRUE, getCompilerConfiguration(Boolean.TRUE),
  )

  @Test
  @Parameters
  @TestCaseName('testCompilation[{index}]: {0}, parameters = {2}')
  void testCompilation(String testName, String source, Boolean parameters) {
    GroovyClassLoader groovyClassLoader = new GroovyClassLoader(Thread.currentThread().contextClassLoader ?: this.class.classLoader, COMPILER_CONFIGURATIONS[parameters])
    groovyClassLoader.parseClass(source)
  }

  @Test
  @Parameters
  @TestCaseName('testCompilationExpected[{index}]: {0}, parameters = {2}')
  void testCompilationExpected(String testName, String source, Boolean parameters) {
    GroovyClassLoader groovyClassLoader = new GroovyClassLoader(Thread.currentThread().contextClassLoader ?: this.class.classLoader, COMPILER_CONFIGURATIONS[parameters])
    groovyClassLoader.parseClass(source)
  }

  private static final Template AST_TEST_TEMPLATE = new StreamingTemplateEngine().createTemplate(Resources.toString(Resources.getResource(this, 'ASTTest.groovy.template'), Charsets.UTF_8))

  @Test
  @Parameters
  @TestCaseName('testASTMatch[{index}]: {0}, parameters = {3}, compilePhase = {4}')
  void testASTMatch(String testName, String source, URL expectedUrl, Boolean parameters, CompilePhase compilePhase) {
    String sourceWithASTTest = source.replaceFirst(DECLARATION_PATTERN, AST_TEST_TEMPLATE.make(
      compilePhase: compilePhase.inspect(),
      expectedUrl: expectedUrl.toString().inspect(),
    ).toString())
    GroovyClassLoader groovyClassLoader = new GroovyClassLoader(Thread.currentThread().contextClassLoader ?: this.class.classLoader, COMPILER_CONFIGURATIONS[parameters])
    groovyClassLoader.parseClass(sourceWithASTTest)
  }

  @Test
  @Parameters
  @TestCaseName('testSerialization[{index}]: {0}')
  void testSerialization(String testName, File source, File source2) {
    // assertScript source
    GroovyClassLoader groovyClassLoader = new GroovyClassLoader(Thread.currentThread().contextClassLoader ?: this.class.classLoader, COMPILER_CONFIGURATIONS[Boolean.FALSE])
    groovyClassLoader.parseClass(source)
    /*groovyClassLoader.parseClass(source2)*/
    GroovyShell shell = new GroovyShell(groovyClassLoader)
    // shell.evaluate(source)
    shell.evaluate(source2)
  }

  private static Object[] parametersForTestErroneousSource() {
    Object[] result = ClassPath.from(this.classLoader).resources.findResults { ClassPath.ResourceInfo it ->
      Matcher m = it.resourceName =~ '\\Acom/github/hashicorp/packer/engine/ast/erroneous/(\\S+)/source\\.groovy\\z'
      if (m) {
        String testName = m[0][1]
        [testName, Resources.toString(it.url(), Charsets.UTF_8), Resources.readLines(Paths.get(it.url().toURI()).parent.resolve('errorMessages.txt').toUri().toURL(), Charsets.UTF_8)].toArray(new Object[3])
      } else {
        null
      }
    }.toArray()
    assert result.length > 0
    result
  }

  private static Object[] parametersForTestCompilation() {
    Object[] result = GroovyCollections.combinations([
      ClassPath.from(this.classLoader).resources.findResults { ClassPath.ResourceInfo it ->
        Matcher m = it.resourceName =~ '\\Acom/github/hashicorp/packer/engine/ast/valid/(\\S+)/source\\.groovy\\z'
        if (m) {
          String testName = m[0][1]
          [testName, Resources.toString(it.url(), Charsets.UTF_8)]
        } else {
          null
        }
      },
      [
        Boolean.FALSE,
        Boolean.TRUE
      ],
    ]).collect { it.flatten().toArray(new Object[3]) }.toArray()
    assert result.length > 0
    result
  }

  private static Object[] parametersForTestCompilationExpected() {
    Object[] result = (
      ClassPath.from(this.classLoader).resources.findResults { ClassPath.ResourceInfo it ->
        Matcher m = it.resourceName =~ '\\Acom/github/hashicorp/packer/engine/ast/valid/(\\S+)/expected/withoutParameters\\.groovy\\z'
        if (m) {
          String testName = m[0][1]
          [testName, Resources.toString(it.url(), Charsets.UTF_8), Boolean.FALSE].toArray(new Object[3])
        } else {
          null
        }
      } + ClassPath.from(this.classLoader).resources.findResults { ClassPath.ResourceInfo it ->
        Matcher m = it.resourceName =~ '\\Acom/github/hashicorp/packer/engine/ast/valid/(\\S+)/expected/withParameters\\.groovy\\z'
        if (m) {
          String testName = m[0][1]
          [testName, Resources.toString(it.url(), Charsets.UTF_8), Boolean.TRUE].toArray(new Object[3])
        } else {
          null
        }
      }
    ).toArray()
    assert result.length > 0
    result
  }

  private static Object[] parametersForTestASTMatch() {
    Object[] result = GroovyCollections.combinations([
      ClassPath.from(this.classLoader).resources.findResults { ClassPath.ResourceInfo it ->
        Matcher m = it.resourceName =~ '\\Acom/github/hashicorp/packer/engine/ast/valid/(\\S+)/source\\.groovy\\z'
        if (m) {
          String testName = m[0][1]
          [testName, it.url()]
        } else {
          null
        }
      }.collectMany { it ->
        def newIt = [it[0], Resources.toString(it[1], Charsets.UTF_8)]
        Path expectedPath = Paths.get(it[1].toURI()).parent.resolve('expected')
        [
          newIt + [expectedPath.resolve('withoutParameters.groovy').toUri().toURL(), Boolean.FALSE],
          newIt + [expectedPath.resolve('withParameters.groovy').toUri().toURL(), Boolean.TRUE],
        ]
      },
      [
        CompilePhase.SEMANTIC_ANALYSIS,
        /* TODO
        CompilePhase.CANONICALIZATION,
        CompilePhase.INSTRUCTION_SELECTION,
        CompilePhase.CLASS_GENERATION,*/
      ],
    ]).collect { it.flatten().toArray(new Object[5]) }.toArray()
    assert result.length > 0
    result
  }

  private static Object[] parametersForTestSerialization() {
    Object[] result = ClassPath.from(this.classLoader).resources.findResults { ClassPath.ResourceInfo it ->
      Matcher m = it.resourceName =~ '\\Acom/github/hashicorp/packer/engine/ast/valid/(\\S+)/serialization\\.groovy\\z'
      if (m) {
        String testName = m[0][1]
        // [testName, Resources.toString(Paths.get(it.url().toURI()).resolveSibling('source.groovy').toUri().toURL(), Charsets.UTF_8) + Resources.toString(it.url(), Charsets.UTF_8)].toArray(new Object[2])
        // [testName, Resources.toString(Paths.get(it.url().toURI()).resolveSibling('source.groovy').toUri().toURL(), Charsets.UTF_8), Resources.toString(it.url(), Charsets.UTF_8)].toArray(new Object[3])
        [testName, Paths.get(it.url().toURI()).resolveSibling('source.groovy').toFile(), new File(it.url().toURI())].toArray(new Object[3])
      } else {
        null
      }
    }.toArray()
    assert result.length > 0
    result
  }

  private ClassLoader getClassLoader1() { // TODO
    Thread.currentThread().contextClassLoader ?: this.class.classLoader
  }
}
