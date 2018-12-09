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
import static org.codehaus.groovy.ast.ClassHelper.make
import static org.codehaus.groovy.ast.ClassHelper.makeCached
import static org.codehaus.groovy.ast.tools.WideningCategories.implementsInterfaceOrSubclassOf
import static org.codehaus.groovy.ast.ClassNode.ACC_PUBLIC
import static org.codehaus.groovy.ast.ClassNode.ACC_ABSTRACT
import static org.codehaus.groovy.ast.ClassNode.ACC_PRIVATE
import static org.codehaus.groovy.ast.ClassNode.ACC_STATIC
import static org.codehaus.groovy.ast.ClassNode.ACC_FINAL
import static org.codehaus.groovy.ast.tools.GenericsUtils.newClass
import static org.codehaus.groovy.ast.tools.GenericsUtils.makeDeclaringAndActualGenericsTypeMap
import static org.codehaus.groovy.ast.tools.GenericsUtils.findActualTypeByGenericsPlaceholderName
import org.codehaus.groovy.control.messages.SyntaxErrorMessage
import org.codehaus.groovy.syntax.SyntaxException
import org.codehaus.groovy.ast.AnnotatedNode
import org.codehaus.groovy.transform.sc.StaticCompileTransformation
import com.fasterxml.jackson.annotation.JsonAlias
import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.databind.PropertyNamingStrategy
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
  private static final ConstantExpression NULL = constX(null)
  private static final VariableExpression THIS_X = varX('this')
  private static final String DOLLAR = '$'
  private static final ClassNode JSON_PROPERTY_CLASS = makeCached(JsonProperty)
  private static final ClassNode JSON_ALIAS_CLASS = makeCached(JsonAlias)
  private static final ClassNode INTERPOLABLE_VALUE_CLASS = makeCached(InterpolableValue)
  private static final ClassNode INTERPOLABLE_OBJECT_CLASS = makeCached(InterpolableObject)
  private static final ClassNode DEFAULT_CLASS = makeCached(Default)
  private static final ClassNode IGNORE_IF_CLASS = makeCached(IgnoreIf)
  private static final ClassNode POST_PROCESS_CLASS = makeCached(PostProcess)
  private static final String FROM = 'from'
  private static final AnnotationNode OVERRIDE_ANNOTATION = new AnnotationNode(makeCached(Override))
  private static final ClassNode JSON_CREATOR_CLASS = makeCached(JsonCreator)
  private static final ClassNode COMPILE_STATIC_CLASS = makeCached(CompileStatic)
  private static final AnnotationNode COMPILE_STATIC_ANNOTATION = new AnnotationNode(COMPILE_STATIC_CLASS)
  private static final ClassNode JSON_DESERIALIZE_CLASS = makeCached(JsonDeserialize)
  private static final ClassNode OPTIONAL_CLASS = makeCached(Optional)
  private static final String CONTEXT = 'context'
  private static final ClassNode CONTEXT_CLASS = makeCached(Context)
  private static final Parameter CONTEXT_PARAM = param(CONTEXT_CLASS, CONTEXT)
  private static final VariableExpression CONTEXT_VAR_X = varX(CONTEXT_PARAM)
  private static final String IMMUTABLE_RAW = 'ImmutableRaw'
  private static final String IMPL = 'Impl'

  @Override
  void visit(ASTNode[] astNodes, SourceUnit sourceUnit) {
    AnnotationNode annotationNode = (AnnotationNode)astNodes[0]
    ClassNode interfase = (ClassNode)astNodes[1]
    if (interfase.interface || !(interfase.modifiers & ACC_ABSTRACT)) {
      addErrorOnAnnotation sourceUnit, annotationNode, 'abstract classes only'
      return
    }
    if (!implementsInterfaceOrSubclassOf(interfase, INTERPOLABLE_OBJECT_CLASS)) {
      addErrorOnAnnotation sourceUnit, annotationNode, 'abstract classes implementing InterpolableObject', interfase.interfaces
      return
    }
    if (!getAnnotation(interfase, COMPILE_STATIC_CLASS)) {
      addErrorOnAnnotation sourceUnit, annotationNode, 'statically compiled interfaces only'
      return
    }

    // TOTHINK: No need for this, but test fails
    interfase.annotations.remove(astNodes[0])

    String interfaseName = interfase.nameWithoutPackage
    String interfaseFullName = interfase.name
    String implClassName = "$interfaseName$IMPL"
    String implClassFullName = "$interfaseFullName$DOLLAR$implClassName"

    ClassNode interfaseRef = newClass(interfase)

    ClassNode implClass = (ClassNode) interfase.innerClasses.find { ClassNode clazz -> clazz.nameWithoutPackage == implClassName }
    boolean generateImplClass = implClass == null
    if (generateImplClass) {
      implClass = new InnerClassNode(
        interfaseRef,
        implClassFullName,
        ACC_PUBLIC | ACC_STATIC | ACC_FINAL,
        OBJECT_TYPE, // TODO: remove unnecessary garbage
        [interfaseRef] as ClassNode[],
        [] as MixinNode[]
      )
    }

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
          varX(FROM, interfaseRef),
          fieldNameConstant
        )
        VariableExpression fieldVarX = varX(fieldName)

        /*
         * Due to https://issues.apache.org/jira/browse/GROOVY-8914 inner classes don't work anyway.
         * Maybe we should remove this code too
          */
        String typImplClassName =  "$typ.name$DOLLAR${ isValue ? IMMUTABLE_RAW /* TODO: + Mutable version */ : "$typ.name$IMPL" }"
        ClassNode typImplType = typ.name.startsWith("$interfase.name\$") ? newClass((ClassNode)typ.redirect().innerClasses.find { InnerClassNode innerClassNode -> innerClassNode.name == typImplClassName }) : make(this.class.classLoader.loadClass(typImplClassName))

        AnnotationNode jsonPropertyAnnotation = getAnnotation(method, JSON_PROPERTY_CLASS)
        if (jsonPropertyAnnotation == null && !interfase.compileUnit.config.parameters) {
          String fieldJsonName = ((PropertyNamingStrategy.PropertyNamingStrategyBase)PropertyNamingStrategy.SNAKE_CASE).translate(fieldName)
          jsonPropertyAnnotation = new AnnotationNode(JSON_PROPERTY_CLASS)
          jsonPropertyAnnotation.addMember('value', constX(fieldJsonName))
        }

        AnnotationNode jsonAliasAnnotation = getAnnotation(method, JSON_ALIAS_CLASS)
        if (jsonAliasAnnotation != null) {
          method.annotations.remove(jsonAliasAnnotation)
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
          // block(returnS(thisFieldX))
          block(stmt(thisFieldX))
        )
        methodImpl.addAnnotation(OVERRIDE_ANNOTATION)

        Parameter constructorParameter = param(
          typ,
          fieldName
        )
        if (jsonPropertyAnnotation != null) {
          constructorParameter.addAnnotation(jsonPropertyAnnotation)
        }
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
        AnnotationNode defaultAnnotation = getAnnotation(method, DEFAULT_CLASS)
        AnnotationNode ignoreIfAnnotation = getAnnotation(method, IGNORE_IF_CLASS)
        AnnotationNode postProcessAnnotation = getAnnotation(method, POST_PROCESS_CLASS)

        if (isValue) {
          List<Expression> interpolateValueParams = [(Expression)CONTEXT_VAR_X]

          if (defaultAnnotation != null) {
            method.annotations.remove(defaultAnnotation)
            if (defaultAnnotation.members['dynamic']) {
              interpolateValueParams.add defaultAnnotation.members['value']
            } else {
              ClosureExpression defaultClosure = (ClosureExpression)defaultAnnotation.members['value']
              List<Statement> statements = ((BlockStatement)defaultClosure.code).statements
              int countOfStatements = statements.size()
              if (countOfStatements != 1) {
                addErrorOnDefaultClosure sourceUnit, defaultClosure, 'have exactly one statement', countOfStatements
                return
              }
              Statement firstS = statements.first()
              Expression defaultValueX
              if (ExpressionStatement.isInstance(firstS)) {
                defaultValueX = ((ExpressionStatement)firstS).expression
              } else if (ReturnStatement.isInstance(firstS)) {
                defaultValueX = ((ReturnStatement)firstS).expression
              } else {
                addErrorOnDefaultClosure sourceUnit, firstS, 'have expression or return statement', firstS, 'If you can\'t express it this way, set dynamic member to true'
                return
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
            method.annotations.remove(ignoreIfAnnotation)
            interpolateValueParams.add ignoreIfAnnotation.members['value']
            method.addAnnotation(new AnnotationNode(OPTIONAL_CLASS))
          } else if (postProcessAnnotation != null) {
            interpolateValueParams.add NULL
          }

          if (postProcessAnnotation != null) {
            method.annotations.remove(postProcessAnnotation)
            interpolateValueParams.add postProcessAnnotation.members['value']
          }

          interpolateConstructorStatements.add assignS(
            thisFieldX,
            callX(fromFieldX, 'interpolateValue', args(interpolateValueParams))
          )
        } else {
          if (defaultAnnotation != null) {
            addErrorOnValuesOnlyAnnotation sourceUnit, defaultAnnotation, typ.name
            return
          }
          if (ignoreIfAnnotation != null) {
            addErrorOnValuesOnlyAnnotation sourceUnit, ignoreIfAnnotation, typ.name
            return
          }
          if (postProcessAnnotation != null) {
            addErrorOnValuesOnlyAnnotation sourceUnit, postProcessAnnotation, typ.name
            return
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
      block(
        returnS(
          castX(
            interfaseRef,
            ctorX(
              implClass,
              args(
                CONTEXT_VAR_X,
                THIS_X
              )
            )
          )
        )
      )
    )
    interpolateMethodImpl.addAnnotation(OVERRIDE_ANNOTATION)

    if (generateImplClass) {
      new StaticCompileTransformation().visit([COMPILE_STATIC_ANNOTATION, implClass] as ASTNode[], sourceUnit)

      // sourceUnit.AST.addClass(implClass)
      // interfase.compileUnit.addClass(implClass)
      interfase.module.addClass(implClass)
    }
  }

  private static void addError(SourceUnit sourceUnit, ASTNode node, String message) {
    sourceUnit.errorCollector.addErrorAndContinue new SyntaxErrorMessage(
      new SyntaxException(
        message,
        node.lineNumber, node.columnNumber, node.lastLineNumber, node.lastColumnNumber
      ),
      sourceUnit
    )
  }

  private static void addErrorOnAnnotation(SourceUnit sourceUnit, AnnotationNode annotation, String expectedTargetDescription, Object actual = null) {
    addError sourceUnit, annotation, "@$annotation.classNode.name is applicable to $expectedTargetDescription${ actual ? ". Got: $actual" : ''}"
  }

  private static void addErrorOnValuesOnlyAnnotation(SourceUnit sourceUnit, AnnotationNode valueAnnotation, Object actual = null) {
    addErrorOnAnnotation sourceUnit, valueAnnotation, 'values only', actual
  }

  private static void addErrorOnDefaultClosure(SourceUnit sourceUnit, ASTNode node, String expectedDescription, Object actual, String extraInfo = null) {
    addError sourceUnit, node, sprintf("Closure passed to @${ Default.class.name } should $expectedDescription. Got: %d${ extraInfo ? ". $extraInfo" : ''}", actual)
  }

  // Overcomes the fact that AnnotatedNode#getAnnotations(ClassNode) returns an array
  private static AnnotationNode getAnnotation(AnnotatedNode annotatedNode, ClassNode targetClass) {
    annotatedNode.annotations.find { implementsInterfaceOrSubclassOf(it.classNode, targetClass) }
  }
}
