package org.fidata.gradle.packer.template.annotations

import groovy.transform.CompileStatic
import org.codehaus.groovy.ast.ASTNode
import org.codehaus.groovy.ast.AnnotationNode
import org.codehaus.groovy.ast.ClassNode
import org.codehaus.groovy.ast.FieldNode
import org.codehaus.groovy.ast.MethodNode
import org.codehaus.groovy.ast.builder.AstBuilder
import org.codehaus.groovy.control.CompilePhase
import org.codehaus.groovy.control.SourceUnit
import org.codehaus.groovy.transform.ASTTransformation
import org.codehaus.groovy.transform.GroovyASTTransformation
import org.fidata.gradle.packer.template.internal.InterpolableValue
import org.gradle.api.tasks.Console
import org.gradle.api.tasks.Destroys
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.InputDirectory
import org.gradle.api.tasks.InputFile
import org.gradle.api.tasks.InputFiles
import org.gradle.api.tasks.Internal
import org.gradle.api.tasks.LocalState
import org.gradle.api.tasks.Nested
import org.gradle.api.tasks.OutputDirectories
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.OutputFile
import org.gradle.api.tasks.OutputFiles
import org.gradle.api.tasks.PathSensitive
import org.gradle.api.tasks.SkipWhenEmpty
import java.lang.annotation.Annotation
import java.lang.reflect.ParameterizedType

@GroovyASTTransformation(phase = CompilePhase.SEMANTIC_ANALYSIS/*CANONICALIZATION*/)
@CompileStatic
class DefaultTransformation implements ASTTransformation {
  @Override
  void visit(ASTNode[] astNodes, SourceUnit sourceUnit) {
    AnnotationNode annotation = (AnnotationNode)astNodes[0]
    FieldNode field = (FieldNode)astNodes[1]

    if (!InterpolableValue.isInstance(field.type.typeClass)) {
      throw new IllegalArgumentException('Default annotation can be used on fields of InterpolableValue type only')
    }

    Object defaultValue = annotation.getMember('value')
    if (defaultValue == null) {
      throw new NullPointerException('Default value should be not null')
    }

    Class<? extends Serializable> target = (Class<? extends Serializable>)(((ParameterizedType)field.type.typeClass.genericSuperclass).actualTypeArguments[1])
    defaultValue = defaultValue.asType(target)

    List<? extends Class> allAnnotations = (List<? extends Class>)field.annotations*.classNode*.typeClass
    Collection<? extends Class> prohibitedAnnotations = allAnnotations.intersect((List<? extends Class>)[Nested, Optional, SkipWhenEmpty])
    if (prohibitedAnnotations?.size() > 0) {
      throw new IllegalArgumentException(sprintf('Annotations %s are prohibited on fields with defaults', prohibitedAnnotations*.simpleName.join(', ')))
    }

    Collection<? extends Class> annotations = allAnnotations.intersect((List<? extends Class>)[Input, InputFile, InputFiles, InputDirectory, OutputFile, OutputFiles, OutputDirectory, Console, Internal, Destroys, LocalState, OutputDirectories, PathSensitive])
    field.annotations.removeAll { AnnotationNode annotationNode -> annotations.contains(annotationNode.classNode.typeClass) }
    field.annotations.add(new AnnotationNode(new ClassNode(Internal)))

    String methodName = "getInterpolated${ field.name.capitalize() }".toString()

    MethodNode getWithDefault = ((ClassNode)(new AstBuilder().buildFromString("""\
      class GetWithDefault {
        @org.gradle.api.tasks.Internal
        ${ target.canonicalName } $methodName() {
          ${ annotations.collect { Class aClass -> "@$aClass.canonicalName" }.join('\n') }
          ${ field.name }?.interpolatedValue ?: ${ defaultValue.inspect() } 
        }
      }
      """
    ).get(0))).methods.find { MethodNode methodNode -> methodNode.name == methodName }

    field.owner.addMethod(getWithDefault)
  }
}
