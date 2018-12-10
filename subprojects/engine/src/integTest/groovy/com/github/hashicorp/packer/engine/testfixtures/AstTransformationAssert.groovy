package com.github.hashicorp.packer.engine.testfixtures

import static com.google.common.base.Charsets.UTF_8
import static org.codehaus.groovy.ast.builder.AstAssert.assertSyntaxTree
import static org.codehaus.groovy.ast.tools.WideningCategories.implementsInterfaceOrSubclassOf
import static org.codehaus.groovy.ast.ClassHelper.makeCached
import groovy.transform.ASTTest
import groovy.transform.CompileStatic
import org.codehaus.groovy.ast.ASTNode
import org.codehaus.groovy.ast.AnnotationNode
import org.codehaus.groovy.ast.ClassNode
import org.codehaus.groovy.ast.InnerClassNode
import org.codehaus.groovy.ast.builder.AstBuilder
import org.codehaus.groovy.control.CompilePhase
import com.google.common.io.Resources

@CompileStatic
class AstTransformationAssert {
  private static final Closure CLASS_SORTER = { ClassNode classNode ->
    classNode.name
  }
  public static final ClassNode AST_TEST_CLASS = makeCached(ASTTest)

  static void assertAstTransformation(CompilePhase compilePhase, URL expectedUrl, ClassNode node, Class annotationUnderTest) {
    List<ClassNode> expected = ((List<ClassNode>)new AstBuilder().buildFromString(compilePhase, false, Resources.toString(expectedUrl, UTF_8)).findAll { ASTNode astNode ->
      ClassNode.isInstance(astNode)
    }).sort(CLASS_SORTER)

    List<ClassNode> actual = node.module.classes.each { ClassNode classNode ->
      if (!InnerClassNode.isInstance(classNode)) {
        classNode.annotations.removeAll { AnnotationNode annotationNode ->
          implementsInterfaceOrSubclassOf(annotationNode.classNode, AST_TEST_CLASS) ||
          implementsInterfaceOrSubclassOf(annotationNode.classNode, makeCached(annotationUnderTest))
        }
      }
    }.sort(CLASS_SORTER)

    assertSyntaxTree expected, actual
  }

  // Suppress default constructor for noninstantiability
  private AstTransformationAssert() {
    throw new UnsupportedOperationException()
  }
}
