package com.github.hashicorp.packer.engine.ast

import com.github.hashicorp.packer.engine.types.InterpolableObject
import groovy.transform.CompileStatic
import org.codehaus.groovy.ast.ASTNode
import org.codehaus.groovy.ast.AnnotationNode
import org.codehaus.groovy.ast.ClassNode
import org.codehaus.groovy.ast.InnerClassNode
import org.codehaus.groovy.ast.MethodNode
import org.codehaus.groovy.ast.builder.AstBuilder
import org.codehaus.groovy.ast.expr.ClassExpression
import org.codehaus.groovy.control.CompilePhase
import org.codehaus.groovy.control.SourceUnit
import org.codehaus.groovy.transform.ASTTransformation
import org.codehaus.groovy.transform.GroovyASTTransformation
import com.fasterxml.jackson.databind.annotation.JsonDeserialize

@CompileStatic
@GroovyASTTransformation(phase= CompilePhase.SEMANTIC_ANALYSIS)
class InterpolableObjectASTTransformation implements ASTTransformation {
  @Override
  void visit(ASTNode[] astNodes, SourceUnit sourceUnit) {
// TODO
//    List<ClassNode> clazzes = sourceUnit.AST.classes.findAll { ClassNode classNode ->
//      !classNode.interface && (classNode.modifiers & ClassNode.ACC_ABSTRACT) && classNode.implementsInterface(new ClassNode(InterpolableObject))
//    }
//    clazzes.each { ClassNode clazz ->
//      String clazzNameWithoutPackage = clazz.nameWithoutPackage
//      String implClassName = "${ clazzNameWithoutPackage }Impl"
//      ClassNode implClass = (ClassNode)new AstBuilder().buildFromSpec {
//        innerClass(implClassName, ClassNode.ACC_PUBLIC) {
//          classNode(clazzNameWithoutPackage, ClassNode.ACC_PUBLIC | ClassNode.ACC_STATIC | ClassNode.ACC_FINAL) {
//            /*interfaces {
//              classNode InterpolableObject
//            }*/
//          }
//          // classNode Object
//          methods {
//            clazz.methods.each { MethodNode methodNode1 ->
//              if (methodNode1.name.startsWith('get')) {
//                String fieldName = methodNode1.name.substring(3).uncapitalize()
//                method(methodNode1.name, ClassNode.ACC_PUBLIC, methodNode1.returnType) {
//                  ifStatement {
//                    booleanExpression {
//                      staticMethodCall(((ClassNode) (methodNode1.returnType.innerClasses.find { InnerClassNode innerClassNode -> innerClassNode.name == 'Utils' })).typeClass, 'requiresInitialization') {
//                        argumentList {
//                          expression {
//                            field {
//
//                            }
//                          }
//                        }
//                      }
//                    }
//                    block {
//                      staticMethodCall(((ClassNode) (methodNode1.returnType.innerClasses.find { InnerClassNode innerClassNode -> innerClassNode.name == 'Utils' })).typeClass, 'initWithDefault') {
//
//                      }
//                    }
//                    empty()
//                  }
//                  returnStatement {
//
//                    expression
//                  }
//                  field {
//
//                  }
//
//
//                }
//              }
//
//            }
//          }
//          interfaces {
//            classNode(clazzNameWithoutPackage, ClassNode.ACC_PUBLIC) {}
//          }
//        }
//      }.first()
//      clazz.module.addClass implClass
//      clazz.addAnnotation((AnnotationNode)new AstBuilder().buildFromSpec {
//          annotation(JsonDeserialize) {
//            member('as') {
//              new ClassExpression(implClass)
//            }
//          }
//        }.first()
//      )
//    }
  }
}
