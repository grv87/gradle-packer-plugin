package com.github.hashicorp.packer.engine.ast

import static org.codehaus.groovy.ast.tools.GeneralUtils.binX
import static org.codehaus.groovy.ast.tools.GeneralUtils.boolX
import static org.codehaus.groovy.ast.tools.GeneralUtils.varX
import static org.codehaus.groovy.ast.tools.GeneralUtils.constX
import static org.codehaus.groovy.ast.tools.GeneralUtils.ctorX
import static org.codehaus.groovy.ast.tools.GeneralUtils.classX
import static org.codehaus.groovy.ast.tools.GeneralUtils.attrX
import static org.codehaus.groovy.ast.tools.GeneralUtils.NE
import static org.codehaus.groovy.ast.tools.GeneralUtils.ternaryX
import static org.codehaus.groovy.ast.tools.GeneralUtils.castX
import static org.codehaus.groovy.ast.tools.GeneralUtils.assignS
import static org.codehaus.groovy.ast.tools.GeneralUtils.args
import static org.codehaus.groovy.ast.tools.GeneralUtils.block
import static org.codehaus.groovy.ast.tools.GeneralUtils.stmt
import static org.codehaus.groovy.ast.tools.GeneralUtils.propX
import static org.codehaus.groovy.ast.tools.GeneralUtils.param
import static org.codehaus.groovy.ast.tools.GeneralUtils.callX
import static org.codehaus.groovy.ast.tools.GeneralUtils.returnS
import static org.codehaus.groovy.ast.ClassHelper.OBJECT_TYPE
import static org.codehaus.groovy.ast.ClassNode.EMPTY_ARRAY
import static org.codehaus.groovy.ast.ClassHelper.makeCached
import static org.codehaus.groovy.ast.tools.WideningCategories.implementsInterfaceOrSubclassOf
import static org.codehaus.groovy.ast.ClassNode.ACC_PUBLIC
import static org.codehaus.groovy.ast.ClassNode.ACC_PRIVATE
import static org.codehaus.groovy.ast.ClassNode.ACC_STATIC
import static org.codehaus.groovy.ast.ClassNode.ACC_FINAL
import static org.codehaus.groovy.ast.tools.GenericsUtils.newClass
import static org.codehaus.groovy.ast.tools.GenericsUtils.makeDeclaringAndActualGenericsTypeMap
import static org.codehaus.groovy.ast.tools.GenericsUtils.findActualTypeByGenericsPlaceholderName
import com.fasterxml.jackson.annotation.JsonAlias
import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.databind.PropertyNamingStrategy
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
import org.codehaus.groovy.ast.ClassNode
import org.codehaus.groovy.ast.ConstructorNode
import org.codehaus.groovy.ast.FieldNode
import org.codehaus.groovy.ast.InnerClassNode
import org.codehaus.groovy.ast.MethodNode
import org.codehaus.groovy.ast.MixinNode
import org.codehaus.groovy.ast.Parameter
import org.codehaus.groovy.ast.expr.ClosureExpression
import org.codehaus.groovy.ast.expr.ConstantExpression
import org.codehaus.groovy.ast.expr.Expression
import org.codehaus.groovy.ast.expr.VariableExpression
import org.codehaus.groovy.ast.stmt.BlockStatement
import org.codehaus.groovy.ast.stmt.ExpressionStatement
import org.codehaus.groovy.ast.stmt.ReturnStatement
import org.codehaus.groovy.ast.stmt.Statement
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
  private static final VariableExpression CONTEXT_VAR_X = varX(CONTEXT)
  private static final ConstantExpression NULL = constX(null)
  private static final VariableExpression THIS_X = varX('this')
  private static final String DOLLAR = '$'
  public static final ClassNode JSON_PROPERTY_CLASS = makeCached(JsonProperty)
  public static final ClassNode JSON_ALIAS_CLASS = makeCached(JsonAlias)
  public static final ClassNode INTERPOLABLE_VALUE_CLASS = makeCached(InterpolableValue)
  public static final ClassNode INTERPOLABLE_OBJECT_CLASS = makeCached(InterpolableObject)
  public static final ClassNode DEFAULT_CLASS = makeCached(Default)
  public static final ClassNode IGNORE_IF_CLASS = makeCached(IgnoreIf)
  public static final ClassNode POST_PROCESS_CLASS = makeCached(PostProcess)
  public static final String FROM = 'from'
  public static final ClassNode OVERRIDE_CLASS = makeCached(Override)
  public static final AnnotationNode OVERRIDE_ANNOTATION = new AnnotationNode(OVERRIDE_CLASS)
  public static final ClassNode JSON_CREATOR_CLASS = makeCached(JsonCreator)
  public static final ClassNode COMPILE_STATIC_CLASS = makeCached(CompileStatic)
  public static final ClassNode JSON_DESERIALIZE_CLASS = makeCached(JsonDeserialize)
  public static final ClassNode OPTIONAL_CLASS = makeCached(Optional)
  public static final ClassNode CONTEXT_CLASS = makeCached(Context)
  public static final Parameter CONTEXT_PARAM = param(CONTEXT_CLASS, CONTEXT)

  @Override
  void visit(ASTNode[] astNodes, SourceUnit sourceUnit) {
    ClassNode interfase = (ClassNode)astNodes[1]
    if (!interfase.interface) {
      throw new IllegalArgumentException('AutoImplement annotations should be applied to interfaces')
    }
    if (!implementsInterfaceOrSubclassOf(interfase, INTERPOLABLE_OBJECT_CLASS)) { 
      throw new IllegalArgumentException(sprintf('AutoImplement annotations should be applied to interfaces extending InterpolableObject. Got: %s', interfase.interfaces))
    }
    // TODO: Check for CompileStatic ?

    // TOTHINK: No need for this
    // assert interfase.annotations.removeAll { AnnotationNode node -> implementsInterfaceOrSubclassOf(node.classNode, make(AutoImplement)) }
    // assert interfase.annotations.remove(astNodes[0])

    String interfaseName = interfase.nameWithoutPackage
    String interfaseFullName = interfase.name
    String implClassName = "${ interfaseName }Impl"
    String implClassFullName = "$interfaseFullName$DOLLAR$implClassName"

    ClassNode interfaseRef = newClass(interfase)

    ClassNode implClass = (ClassNode)interfase.innerClasses.find { ClassNode clazz -> clazz.nameWithoutPackage == implClassName } ?: new InnerClassNode(
      interfaseRef,
      implClassFullName,
      ACC_PUBLIC | ACC_STATIC | ACC_FINAL,
      OBJECT_TYPE, // TODO: remove unnecessary garbage
      [interfaseRef] as ClassNode[],
      [] as MixinNode[]
    )
    implClass.addAnnotation(new AnnotationNode(COMPILE_STATIC_CLASS)) // !!!
    
    AnnotationNode jsonDeserializeAnnotation = new AnnotationNode(JSON_DESERIALIZE_CLASS)
    jsonDeserializeAnnotation.addMember('as', classX(implClass))
    interfase.addAnnotation(jsonDeserializeAnnotation)

    List<Parameter> constructorParams = []

    List<Statement> defaultConstructorStatements = []
    List<Statement> interpolateConstructorStatements = []

    interfase.methods.each { MethodNode method ->
      Matcher m = GETTER_PATTERN.matcher(method.name)
      ClassNode typ = method.returnType

      boolean isValue = implementsInterfaceOrSubclassOf(typ, INTERPOLABLE_VALUE_CLASS)
      ClassNode targetClass
      if (isValue) {
         targetClass = findActualTypeByGenericsPlaceholderName('Target', makeDeclaringAndActualGenericsTypeMap(INTERPOLABLE_VALUE_CLASS, typ))
      }

      if (m && method.parameters.length == 0) {
        String fieldName = m.replaceFirst('').uncapitalize()
        ConstantExpression fieldNameConstant = constX(fieldName)
        Expression thisFieldX = attrX(
          THIS_X,
          fieldNameConstant
        )
        Expression fromFieldX = propX(
          varX(FROM),
          fieldNameConstant
        )
        VariableExpression fieldVarX = varX(fieldName)

        // String typImplClassName = isValue ? "$typ.name${ DOLLAR }ImmutableRaw" /* TODO */ : "$typ.name$DOLLAR${typ.name}Impl"
        String typImplClassName = isValue ? 'ImmutableRaw' : "${typ.name}Impl"
        ClassNode typImplType = (ClassNode)typ.redirect().innerClasses.find { InnerClassNode innerClassNode -> innerClassNode.nameWithoutPackage == typImplClassName }

        AnnotationNode jsonPropertyAnnotation = method.annotations.find { AnnotationNode it -> implementsInterfaceOrSubclassOf(it.classNode, JSON_PROPERTY_CLASS) }
        if (jsonPropertyAnnotation == null) {
          String fieldJsonName = ((PropertyNamingStrategy.PropertyNamingStrategyBase)PropertyNamingStrategy.SNAKE_CASE).translate(fieldName)
          jsonPropertyAnnotation = new AnnotationNode(JSON_PROPERTY_CLASS)
          jsonPropertyAnnotation.addMember('value', constX(jsonDeserializeAnnotation))
          method.addAnnotation jsonPropertyAnnotation
        }

        AnnotationNode jsonAliasAnnotation = method.annotations.find { implementsInterfaceOrSubclassOf(it.classNode, JSON_ALIAS_CLASS) }
        if (jsonAliasAnnotation != null) {
          assert method.annotations.remove(jsonAliasAnnotation)
        }

        implClass.addField(
          fieldName,
          ACC_PRIVATE | ACC_FINAL,
          typ,
          null
        )

        MethodNode methodImpl = implClass.addMethod(
          method.name,
          ACC_PUBLIC,
          typ,
          method.parameters,
          method.exceptions,
          returnS(thisFieldX)
        )
        methodImpl.addAnnotation(OVERRIDE_ANNOTATION)

        Parameter constructorParameter = param(
          typ,
          fieldName
        )
        constructorParameter.addAnnotation(jsonPropertyAnnotation)
        if (jsonAliasAnnotation != null) {
          constructorParameter.addAnnotation(jsonAliasAnnotation)
        }
        constructorParams.add constructorParameter

        defaultConstructorStatements.add assignS(
          thisFieldX,
          ternaryX(
            boolX(
              binX(
                fieldVarX,
                NE,
                NULL
              )
            ),
            fieldVarX,
            ctorX(typImplType)
          )
        )

        // TODO: + Context vars
        AnnotationNode defaultAnnotation = method.annotations.find { AnnotationNode it -> implementsInterfaceOrSubclassOf(it.classNode, DEFAULT_CLASS) }
        AnnotationNode ignoreIfAnnotation = method.annotations.find { AnnotationNode it -> implementsInterfaceOrSubclassOf(it.classNode, IGNORE_IF_CLASS) }
        AnnotationNode postProcessAnnotation = method.annotations.find { AnnotationNode it -> implementsInterfaceOrSubclassOf(it.classNode, POST_PROCESS_CLASS) }

        if (isValue) {
          List<Expression> interpolateValueParams = [(Expression)CONTEXT_VAR_X]

          if (defaultAnnotation != null) {
            assert method.annotations.remove(defaultAnnotation)
            if (defaultAnnotation.members['dynamic']) {
              interpolateValueParams.add defaultAnnotation.members['value']
            } else {
              List<Statement> statements = ((BlockStatement)((ClosureExpression)(defaultAnnotation.members['value'])).code).statements
              int countOfStatements = statements.size()
              if (countOfStatements != 1) {
                throw new IllegalArgumentException(sprintf('Closure passed to Default annotation should have exactly one statement. Got: %d', countOfStatements))
              }
              Statement firstS = statements.first()
              Expression defaultValueX
              if (ExpressionStatement.isInstance(firstS)) {
                defaultValueX = ((ExpressionStatement)firstS).expression
              } else if (ReturnStatement.isInstance(firstS)) {
                defaultValueX = ((ReturnStatement)firstS).expression
              } else {
                throw new IllegalArgumentException(sprintf('Closure passed to Default annotation should have expression or return statement. Got: %s', firstS))
              }
              interpolateValueParams.add defaultValueX
            }
          } else if (ignoreIfAnnotation != null || postProcessAnnotation != null) {
            interpolateValueParams.add castX(
              targetClass,
              NULL
            )
          }

          if (ignoreIfAnnotation != null) {
            assert method.annotations.remove(ignoreIfAnnotation)
            interpolateValueParams.add ignoreIfAnnotation.members['value']
            method.addAnnotation(new AnnotationNode(OPTIONAL_CLASS))
          } else if (postProcessAnnotation != null) {
            interpolateValueParams.add NULL
          }

          if (postProcessAnnotation != null) {
            assert method.annotations.remove(postProcessAnnotation)
            interpolateValueParams.add postProcessAnnotation.members['value']
          }

          interpolateConstructorStatements.add assignS(
            thisFieldX,
            callX(fromFieldX, 'interpolateValue', args(interpolateValueParams))
          )
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

          interpolateConstructorStatements.add assignS(
            thisFieldX,
            callX(
              fromFieldX,
              'interpolate',
              args(CONTEXT_VAR_X)
            )
          )
        }
      }
    }

    ConstructorNode defaultConstructor = implClass.addConstructor(
      ACC_PUBLIC,
      constructorParams.toArray(new Parameter[0]),
      EMPTY_ARRAY,
      block(
        null,
        defaultConstructorStatements
      ) // TODO: need to call super() ?
    )
    defaultConstructor.addAnnotation(new AnnotationNode(JSON_CREATOR_CLASS))

    implClass.addConstructor(
      ACC_PRIVATE,
      [
        CONTEXT_PARAM,
        param(
          interfaseRef, FROM
        )
      ].toArray(new Parameter[2]),
      EMPTY_ARRAY,
      block(
        null,
        interpolateConstructorStatements
      ) // TODO: need to call super() ?
    )

    MethodNode interpolateMethodImpl = implClass.addMethod(
      'interpolate',
      ACC_PUBLIC,
      interfaseRef,
      [
        CONTEXT_PARAM
      ].toArray(new Parameter[1]),
      EMPTY_ARRAY,
      stmt(
        ctorX(
          implClass,
          args(
            CONTEXT_VAR_X,
            THIS_X
          )
        )
      )
    )
    interpolateMethodImpl.addAnnotation(OVERRIDE_ANNOTATION)

    interfase.module.addClass(implClass)
  }
}
