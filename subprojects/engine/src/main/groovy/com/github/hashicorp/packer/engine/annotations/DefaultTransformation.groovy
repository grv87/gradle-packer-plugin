package com.github.hashicorp.packer.engine.annotations

import groovy.transform.CompileStatic
import org.codehaus.groovy.ast.ASTNode
import org.codehaus.groovy.ast.AnnotationNode
import org.codehaus.groovy.ast.stmt.Statement
import org.codehaus.groovy.ast.ClassHelper
import org.codehaus.groovy.ast.ClassNode
import org.codehaus.groovy.ast.FieldNode
import org.codehaus.groovy.ast.MethodNode
import org.codehaus.groovy.ast.Parameter
import org.codehaus.groovy.ast.builder.AstBuilder
import org.codehaus.groovy.ast.tools.GenericsUtils
import org.codehaus.groovy.control.CompilePhase
import org.codehaus.groovy.control.SourceUnit
import org.codehaus.groovy.transform.ASTTransformation
import org.codehaus.groovy.transform.GroovyASTTransformation
import org.codehaus.groovy.ast.expr.ConstantExpression
import com.github.hashicorp.packer.engine.types.InterpolableValue
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

@GroovyASTTransformation(phase = CompilePhase.CANONICALIZATION)
@CompileStatic
@SuppressWarnings('CatchRuntimeException') // TODO
class DefaultTransformation implements ASTTransformation {
  @Override
  void visit(ASTNode[] astNodes, SourceUnit sourceUnit) {
    /*try {
      AnnotationNode annotation = (AnnotationNode) astNodes[0]
      FieldNode field = (FieldNode) astNodes[1]

      ClassNode interpolableValueClass = ClassHelper.make(InterpolableValue)
      if (!field.type.isDerivedFrom(interpolableValueClass)) {
        throw new IllegalArgumentException(sprintf('Default annotation can be used on fields of InterpolableValue type only. Got: %s', [field.type.name]))
      }

      String defaultValue = ((ConstantExpression)annotation.getMember('value')).text
      if (defaultValue == null) {
        throw new IllegalArgumentException('Default value should be not null')
      }
      // throw new IllegalStateException(defaultValue.toString())

      Map<String, ClassNode> spec = [:]
      GenericsUtils.extractSuperClassGenerics(field.type, interpolableValueClass, spec)

      // throw new IllegalStateException(spec.toString())

      ClassNode target = spec['Target'] // .redirect() // .find { Map.Entry<String, ClassNode> entry -> entry.key.startsWith('Target=') }.value // GenericsUtils.extractPlaceholders(field.type) // .superClass.genericsTypes[1].type.redirect() // TODO
      // throw new IllegalStateException(target.toString())
      // .superClass.genericsTypes[1].type
      // defaultValue = defaultValue.asType(target)

      List<ClassNode> allAnnotations = field.annotations*.classNode
      Collection<ClassNode> prohibitedAnnotations = allAnnotations.intersect([Nested, Optional, SkipWhenEmpty].collect { Class clazz -> ClassHelper.make(clazz) })
      if (prohibitedAnnotations?.size() > 0) {
        throw new IllegalArgumentException(sprintf('Annotations %s are prohibited on fields with defaults', prohibitedAnnotations*.name.join(', ')))
      }

      Collection<ClassNode> annotations = allAnnotations.intersect([Input, InputFile, InputFiles, InputDirectory, OutputFile, OutputFiles, OutputDirectory, Console, Internal, Destroys, LocalState, OutputDirectories, PathSensitive].collect { Class clazz -> ClassHelper.make(clazz) })
      field.annotations.removeAll { AnnotationNode annotationNode -> annotations.contains(annotationNode.classNode.typeClass) }
      field.annotations.add(new AnnotationNode(new ClassNode(Internal)))

      String methodName = "getInterpolated${ field.name.capitalize() }"

      Statement getWithDefault = (Statement) new AstBuilder().buildFromString("""\
        ${ field.name }?.interpolatedValue ?: $defaultValue
      """)[0]

      MethodNode methodNode = new MethodNode(methodName, MethodNode.ACC_PUBLIC, target, new Parameter[0], new ClassNode[0], getWithDefault)
      methodNode.addAnnotations(annotations.collect { ClassNode classNode -> new AnnotationNode(classNode) })

      field.owner.addMethod(methodNode)
    } catch (RuntimeException e) {
      throw new IllegalArgumentException('Error', e)
    }*/
  }
}
