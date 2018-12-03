package com.github.hashicorp.packer.engine.ast

import com.google.common.base.Charsets
import com.google.common.io.Resources
import groovy.transform.CompileStatic
import org.codehaus.groovy.ast.ASTNode
import org.codehaus.groovy.ast.builder.AstAssert
import org.codehaus.groovy.ast.builder.AstBuilder
import org.codehaus.groovy.control.CompilePhase
import org.junit.Test

@CompileStatic
class AutoImplementASTTransformationTest {
  @Test
  void test() { // TODO: evaluate ?
    new GroovyShell(this.class.classLoader).parse(Resources.toString(Resources.getResource('com/github/hashicorp/packer/engine/ast/AutoImplementASTTransformationTest/source.groovy'), Charsets.UTF_8))
  }
}
