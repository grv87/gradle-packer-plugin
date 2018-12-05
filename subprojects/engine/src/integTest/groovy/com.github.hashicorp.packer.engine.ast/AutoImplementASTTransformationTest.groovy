package com.github.hashicorp.packer.engine.ast

import com.google.common.io.Resources
import groovy.transform.CompileStatic
import org.codehaus.groovy.ast.ModuleNode
import org.codehaus.groovy.control.CompilerConfiguration
import org.codehaus.groovy.control.ErrorCollector
import org.codehaus.groovy.control.SourceUnit
import org.junit.Test

@CompileStatic
class AutoImplementASTTransformationTest {
  @Test
  void test() { // TODO: evaluate ?
//    CompilerConfiguration compilerConfiguration = new CompilerConfiguration(/*TODO*/)
//    ErrorCollector errorCollector = new ErrorCollector(compilerConfiguration)
//    ModuleNode expected = new ModuleNode(new SourceUnit(Resources.getResource('com/github/hashicorp/packer/engine/ast/AutoImplementASTTransformationTest/expected.groovy'), compilerConfiguration, new GroovyClassLoader(), errorCollector)) // .getAST()
    GroovyClassLoader groovyClassLoader = new GroovyClassLoader(this.class.classLoader)
    groovyClassLoader.parseClass(new File(Resources.getResource('com/github/hashicorp/packer/engine/ast/AutoImplementASTTransformationTest/source.groovy').toURI()))
  }
}
