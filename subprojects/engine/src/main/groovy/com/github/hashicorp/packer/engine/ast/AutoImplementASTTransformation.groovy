package com.github.hashicorp.packer.engine.ast

import com.fasterxml.jackson.annotation.JsonAlias
import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty
import com.github.hashicorp.packer.engine.annotations.AutoImplement
import com.github.hashicorp.packer.engine.annotations.Default
import com.github.hashicorp.packer.engine.annotations.IgnoreIf
import com.github.hashicorp.packer.engine.annotations.PostProcess
import com.github.hashicorp.packer.engine.types.InterpolableObject
import com.github.hashicorp.packer.engine.types.InterpolableValue
import com.github.hashicorp.packer.template.Context
import groovy.transform.CompileStatic
import org.codehaus.groovy.ast.ASTNode
import org.codehaus.groovy.ast.AnnotationNode
import org.codehaus.groovy.ast.ClassHelper
import org.codehaus.groovy.ast.ClassNode
import org.codehaus.groovy.ast.ConstructorNode
import org.codehaus.groovy.ast.FieldNode
import org.codehaus.groovy.ast.InnerClassNode
import org.codehaus.groovy.ast.MethodNode
import org.codehaus.groovy.ast.MixinNode
import org.codehaus.groovy.ast.Parameter
import org.codehaus.groovy.ast.builder.AstBuilder
import org.codehaus.groovy.ast.expr.ClassExpression
import org.codehaus.groovy.ast.expr.ConstantExpression
import org.codehaus.groovy.ast.expr.ElvisOperatorExpression
import org.codehaus.groovy.ast.expr.Expression
import org.codehaus.groovy.ast.expr.FieldExpression
import org.codehaus.groovy.ast.expr.VariableExpression
import org.codehaus.groovy.ast.stmt.ReturnStatement
import org.codehaus.groovy.ast.stmt.Statement
import org.codehaus.groovy.ast.tools.GeneralUtils
import org.codehaus.groovy.ast.tools.GenericsUtils
import org.codehaus.groovy.control.CompilePhase
import org.codehaus.groovy.control.SourceUnit
import org.codehaus.groovy.transform.ASTTransformation
import org.codehaus.groovy.transform.GroovyASTTransformation
import com.fasterxml.jackson.databind.annotation.JsonDeserialize
import org.gradle.api.tasks.Optional
import java.util.regex.Matcher
import java.util.regex.Pattern

@CompileStatic
@GroovyASTTransformation(phase = CompilePhase.SEMANTIC_ANALYSIS)
class AutoImplementASTTransformation implements ASTTransformation {
  private static final Pattern GETTER_PATTERN = ~/^get/

  private static final String CONTEXT = 'context'
  private static final VariableExpression CONTEXT_VAR_X = GeneralUtils.varX(CONTEXT)
  private static final ConstantExpression NULL = GeneralUtils.constX(null)
  private static final ClassExpression THIS_X = GeneralUtils.classX(ClassNode.THIS)
  private static final String DOLLAR = '$'

  @Override
  void visit(ASTNode[] astNodes, SourceUnit sourceUnit) {
    ClassNode interfase = (ClassNode)astNodes[1]
    if (!interfase.interface) {
      throw new IllegalArgumentException('AutoImplement annotations should be applied to interfaces')
    }
    if (!interfase.implementsInterface(ClassHelper.make(InterpolableObject))) {
    // if (!interfase.interfaces.any { ClassNode it -> InterpolableObject.isAssignableFrom(it.typeClass) }) {
      throw new IllegalArgumentException(sprintf('AutoImplement annotations should be applied to interfaces extending InterpolableObject. Got: %s', interfase.interfaces*.typeClass))
    }
    // TODO: Check for CompileStatic ?

    interfase.annotations.removeAll { AnnotationNode node -> AutoImplement.isAssignableFrom(interfase.annotations[0].classNode.typeClass) }

    GroovyShell groovyShell = new GroovyShell(this.class.classLoader)

    String interfaseName = interfase.nameWithoutPackage
    String interfaseFullName = interfase.name
    String implClassName = "${ interfaseName }Impl"
    String implClassFullName = "$interfaseFullName$DOLLAR$implClassName"

    ClassNode interfaseRef = new ClassNode(interfase.name, interfase.modifiers, interfase.superClass)

    ClassNode implClass = (ClassNode)interfase.innerClasses.find { ClassNode clazz -> clazz.nameWithoutPackage == implClassName }
    if (implClass == null) {
      implClass = new InnerClassNode(
        interfaseRef,
        implClassFullName,
        ClassNode.ACC_PUBLIC | ClassNode.ACC_STATIC | ClassNode.ACC_FINAL,
        ClassHelper.make(Object, false), // TODO: remove unnecessary garbage
        [interfaseRef, ClassHelper.make(GroovyObject, false)] as ClassNode[],
        [] as MixinNode[]
      )
      // implClass = new ClassNode(implClassFullName, ClassNode.ACC_PUBLIC | ClassNode.ACC_STATIC | ClassNode.ACC_FINAL, interfase)
    }

    AnnotationNode jsonDeserializeAnnotation = new AnnotationNode(ClassHelper.make(JsonDeserialize))
    jsonDeserializeAnnotation.addMember('as', GeneralUtils.classX(implClass))
    interfase.addAnnotation(jsonDeserializeAnnotation)

    List<Parameter> constructorParameters = []

    List<Statement> defaultConstructorStatements = []
    List<Statement> interpolateConstructorStatements = []

    interfase.methods.each { MethodNode method ->
      Matcher m = GETTER_PATTERN.matcher(method.name)
      ClassNode typ = method.returnType

      // new CompilationUnit(sourceUnit.AST.unit.config).add

      boolean isValue = InterpolableValue.isAssignableFrom(typ.typeClass)
      ClassNode targetClass
      if (isValue) {
         targetClass = GenericsUtils.findActualTypeByGenericsPlaceholderName('Target', GenericsUtils.makeDeclaringAndActualGenericsTypeMap(ClassHelper.make(InterpolableValue), typ))
      }

      if (m && method.parameters.length == 0) {
        String fieldName = m.replaceFirst('')

        if (method.annotations.any { AnnotationNode it -> it.classNode.typeClass == IgnoreIf } ) {
          method.addAnnotation(new AnnotationNode(ClassHelper.make(Optional)))
        }

        AnnotationNode jsonPropertyAnnotation = method.annotations.find { AnnotationNode it -> it.classNode.typeClass == JsonProperty }
        if (jsonPropertyAnnotation == null) {
          jsonPropertyAnnotation = new AnnotationNode(ClassHelper.make(JsonProperty))
          jsonPropertyAnnotation.addMember('value', GeneralUtils.constX(fieldName))
          method.addAnnotation(jsonPropertyAnnotation)
        }

        AnnotationNode jsonAliasAnnotation = method.annotations.find { AnnotationNode it -> it.classNode.typeClass == JsonAlias }
        if (jsonAliasAnnotation != null) {
          assert method.annotations.remove(jsonAliasAnnotation) /* TODO */
        }

        FieldNode field = implClass.addField(fieldName, ClassNode.ACC_PRIVATE | ClassNode.ACC_FINAL, typ, null)
        FieldExpression fieldX = GeneralUtils.fieldX(field)

        MethodNode methodImpl = implClass.addMethod(
          method.name,
          ClassNode.ACC_PUBLIC,
          typ,
          /*method.parameters*/ new Parameter[0],
          method.exceptions,
          new ReturnStatement(fieldX)
        )
        // TODO: methodImpl.addAnnotation(new AnnotationNode(ClassHelper.make(Override)))

        Parameter constructorParameter = new Parameter(typ, fieldName)
        constructorParameter.addAnnotation(jsonPropertyAnnotation)
        if (jsonAliasAnnotation != null) {
          constructorParameter.addAnnotation(jsonAliasAnnotation)
        }
        constructorParameters.add constructorParameter

        defaultConstructorStatements.add GeneralUtils.assignS(fieldX, new ElvisOperatorExpression(GeneralUtils.varX(fieldName), GeneralUtils.ctorX(ClassHelper.make("$typ.name${ DOLLAR }ImmutableRaw" /* TODO */))))

        // TODO: + Context vars
        AnnotationNode defaultAnnotation = method.annotations.find { AnnotationNode it -> it.classNode.typeClass == Default }
        AnnotationNode ignoreIfAnnotation = method.annotations.find { AnnotationNode it -> it.classNode.typeClass == IgnoreIf }
        AnnotationNode postProcessAnnotation = method.annotations.find { AnnotationNode it -> it.classNode.typeClass == PostProcess }

        Expression thisField = GeneralUtils.propX(THIS_X, fieldX)
        Expression fromField = GeneralUtils.propX(GeneralUtils.varX('from'), fieldX)

        if (isValue) {
          List<Expression> interpolateValueParameters = [(Expression)CONTEXT_VAR_X]

          if (defaultAnnotation != null) {
            method.annotations.remove(defaultAnnotation)
            if (defaultAnnotation.members['dynamic']) {
              interpolateValueParameters.add GeneralUtils.constX(((Closure)groovyShell.evaluate((defaultAnnotation.members['value'].text))).call())
            } else {
              interpolateValueParameters.add defaultAnnotation.members['value']
            }
          } else if (ignoreIfAnnotation != null || postProcessAnnotation != null) {
            interpolateValueParameters.add GeneralUtils.castX(targetClass, NULL)
          }

          if (ignoreIfAnnotation != null) {
            method.annotations.remove(ignoreIfAnnotation)
            interpolateValueParameters.add ignoreIfAnnotation.members['value']
          } else if (postProcessAnnotation != null) {
            interpolateValueParameters.add NULL
          }

          if (postProcessAnnotation != null) {
            method.annotations.remove(postProcessAnnotation)
            interpolateValueParameters.add postProcessAnnotation.members['value']
          }

          interpolateConstructorStatements.add GeneralUtils.assignS(/*fieldX*/thisField, GeneralUtils.callX(fromField, 'interpolateValue', GeneralUtils.args(interpolateValueParameters)))
        } else {
          if (defaultAnnotation != null) {
            throw new IllegalStateException('Default annotation is applicable to values only')
          }
          if (ignoreIfAnnotation != null) {
            throw new IllegalStateException('IgnoreIf annotation is applicable to values only')
          }
          if (postProcessAnnotation != null) {
            throw new IllegalStateException('PostProcess annotation is applicable to values only')
          }

          interpolateConstructorStatements.add GeneralUtils.assignS(/*fieldX*/thisField, GeneralUtils.callX(fromField, 'interpolate', GeneralUtils.args(CONTEXT_VAR_X)))
        }
      }
    }

    ConstructorNode defaultConstructor = implClass.addConstructor(
      ClassNode.ACC_PUBLIC,
      constructorParameters.toArray(new Parameter[0]),
      null,
      GeneralUtils.block(null, defaultConstructorStatements) // TODO: need to call super() ?
    )
    defaultConstructor.addAnnotation(new AnnotationNode(ClassHelper.make(JsonCreator)))

    ConstructorNode interpolateConstructor = implClass.addConstructor(
      ClassNode.ACC_PRIVATE,
      [new Parameter(ClassHelper.make(Context), CONTEXT), new Parameter(interfaseRef, 'raw')].toArray(new Parameter[2]),
      null,
      GeneralUtils.block(null, interpolateConstructorStatements) // TODO: need to call super() ?
    )

    MethodNode interpolateMethodImpl = implClass.addMethod(
      'interpolate',
      ClassNode.ACC_PUBLIC,
      interfaseRef,
      [new Parameter(ClassHelper.make(Context), CONTEXT)].toArray(new Parameter[1]),
      null,
      GeneralUtils.stmt(GeneralUtils.ctorX(implClass, GeneralUtils.args(CONTEXT_VAR_X, THIS_X /* TOTEST or varX('this') */)))
    )
    // TODO: interpolateMethodImpl.addAnnotation(new AnnotationNode(ClassHelper.make(Override)))

    interfase.module.addClass(implClass)
  }
}
