package com.github.hashicorp.packer.engine.ast

import static groovy.test.GroovyAssert.shouldFail
import static groovy.test.GroovyAssertExt.assertScript
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
class AutoImplementASTTransformationTest {
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
    assertScript source, COMPILER_CONFIGURATIONS[parameters]
  }

  private static final Template AST_TEST_TEMPLATE = new StreamingTemplateEngine().createTemplate(Resources.toString(Resources.getResource(this, 'ASTTest.groovy.template'), Charsets.UTF_8))

  @Test
  @Parameters
  @TestCaseName('testASTMatch[{index}]: {0}, parameters = {3}, compilePhase = {4}')
  void testASTMatch(String testName, String source, URL expectedUrl, Boolean parameters, CompilePhase compilePhase) {
    String sourceWithASTTest = source.replaceFirst('^// declaration$', AST_TEST_TEMPLATE.make(
      compilePhase: compilePhase/*.name()*/.inspect(),
      expectedUrl: expectedUrl.inspect()
    ).toString())
    assertScript source, COMPILER_CONFIGURATIONS[parameters]
  }

  private static Object[] parametersForTestErroneousSource() {
    Object[] result = ClassPath.from(this.classLoader).resources.findResults { ClassPath.ResourceInfo it ->
      Matcher m = it.resourceName =~ '\\Acom/github/hashicorp/packer/engine/ast/AutoImplementASTTransformationTest/erroneous/(\\S+)/source\\.groovy\\z'
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
        Matcher m = it.resourceName =~ '\\Acom/github/hashicorp/packer/engine/ast/AutoImplementASTTransformationTest/valid/(\\S+)/source\\.groovy\\z'
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
    ]).flatten().collect { it.toArray(new Object[3]) }.toArray()
    assert result.length > 0
    result
  }


  private static Object[] parametersForTestASTMatch() {
    Object[] result = GroovyCollections.combinations([
      ClassPath.from(this.classLoader).resources.findResults { ClassPath.ResourceInfo it ->
        Matcher m = it.resourceName =~ '\\Acom/github/hashicorp/packer/engine/ast/AutoImplementASTTransformationTest/valid/(\\S+)/source\\.groovy\\z'
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
        CompilePhase.CANONICALIZATION,
        CompilePhase.INSTRUCTION_SELECTION,
        CompilePhase.CLASS_GENERATION,
      ],
    ]).flatten().collect { it.toArray(new Object[5]) }.toArray()
    assert result.length > 0
    result
  }
}
