package com.github.hashicorp.packer.engine.ast

import com.google.common.io.Resources
import groovy.transform.CompileStatic
import org.junit.Test

@CompileStatic
class AutoImplementASTTransformationTest {
  @Test
  void test() { // TODO: evaluate ?
    GroovyClassLoader groovyClassLoader = new GroovyClassLoader(this.class.classLoader)
    groovyClassLoader.parseClass(new File(Resources.getResource('com/github/hashicorp/packer/engine/ast/AutoImplementASTTransformationTest/source.groovy').toURI()))
  }
}
