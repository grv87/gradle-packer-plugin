package com.github.hashicorp.packer.engine.ast

import com.github.hashicorp.packer.engine.types.InterpolableObject
import groovy.transform.CompileStatic
import org.codehaus.groovy.ast.ASTNode
import org.codehaus.groovy.ast.AnnotationNode
import org.codehaus.groovy.ast.ClassNode
import org.codehaus.groovy.ast.builder.AstBuilder
import org.codehaus.groovy.ast.expr.ClassExpression
import org.codehaus.groovy.control.CompilePhase
import org.codehaus.groovy.control.SourceUnit
import org.codehaus.groovy.transform.ASTTransformation
import org.codehaus.groovy.transform.GroovyASTTransformation
import org.gradle.internal.impldep.com.fasterxml.jackson.databind.annotation.JsonDeserialize

@CompileStatic
@GroovyASTTransformation(phase= CompilePhase.SEMANTIC_ANALYSIS)
class InterpolableObjectASTTransformation implements ASTTransformation {
  @Override
  void visit(ASTNode[] astNodes, SourceUnit sourceUnit) {
    List<ClassNode> interfases = sourceUnit.AST.classes.findAll { ClassNode classNode ->
      classNode.interface && classNode.implementsInterface(new ClassNode(InterpolableObject))
    }
    interfases.each { ClassNode interfase ->
      String interfaseNameWithoutPackage = interfase.nameWithoutPackage
      String readOnlyClassName = "${ interfaseNameWithoutPackage }ReadOnly"
      String implClassName = "${ interfaseNameWithoutPackage }Impl"
      ClassNode readOnlyClass = (ClassNode)new AstBuilder().buildFromSpec {
        innerClass(readOnlyClassName, ClassNode.ACC_PUBLIC) {
          classNode(interfaseNameWithoutPackage, ClassNode.ACC_PUBLIC) {
            /*interfaces {
              classNode InterpolableObject
            }*/
          }
          // classNode Object
          interfaces {
            classNode(interfaseNameWithoutPackage, ClassNode.ACC_PUBLIC) {}
          }
        }
      }.first()
      interfase.module.addClass readOnlyClass
      ClassNode implClass = (ClassNode)new AstBuilder().buildFromSpec {
        innerClass(implClassName, ClassNode.ACC_PUBLIC) {
          classNode(interfaseNameWithoutPackage, ClassNode.ACC_PUBLIC) {
            /*interfaces {
              classNode InterpolableObject
            }*/
          }
          // classNode Object
          interfaces {
            classNode(interfaseNameWithoutPackage, ClassNode.ACC_PUBLIC) {}
          }
        }
      }.first()
      interfase.module.addClass implClass
      interfase.addAnnotation((AnnotationNode)new AstBuilder().buildFromSpec {
          annotation(JsonDeserialize) {
            member('as') {
              new ClassExpression(readOnlyClass)
            }
          }
        }.first()
      )
    }
  }
}
