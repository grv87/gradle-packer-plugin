package com.github.hashicorp.packer.engine.ast


import static org.codehaus.groovy.ast.tools.GeneralUtils.ctorSuperS
import static org.codehaus.groovy.ast.tools.GeneralUtils.ctorThisS
import static org.codehaus.groovy.ast.tools.GeneralUtils.varX
import static org.codehaus.groovy.ast.tools.GeneralUtils.constX
import static org.codehaus.groovy.ast.tools.GeneralUtils.ctorX
import static org.codehaus.groovy.ast.tools.GeneralUtils.classX
import static org.codehaus.groovy.ast.tools.GeneralUtils.attrX
import static org.codehaus.groovy.ast.tools.GeneralUtils.castX
import static org.codehaus.groovy.ast.tools.GeneralUtils.assignS
import static org.codehaus.groovy.ast.tools.GeneralUtils.args
import static org.codehaus.groovy.ast.tools.GeneralUtils.block
import static org.codehaus.groovy.ast.tools.GeneralUtils.stmt
import static org.codehaus.groovy.ast.tools.GeneralUtils.propX
import static org.codehaus.groovy.ast.tools.GeneralUtils.param
import static org.codehaus.groovy.ast.tools.GeneralUtils.callX
import static org.codehaus.groovy.ast.tools.GeneralUtils.returnS
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
import static org.codehaus.groovy.ast.ClassNode.EMPTY_ARRAY as EMPTY_CLASS_NODE_ARRAY
import static org.codehaus.groovy.ast.Parameter.EMPTY_ARRAY as EMPTY_PARAMETER_ARRAY
import com.github.hashicorp.packer.engine.utils.Mutability
import com.google.common.collect.ImmutableMap
import com.github.hashicorp.packer.engine.utils.ObjectMapperFacade
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
import org.gradle.api.tasks.Optional
import java.util.regex.Matcher
import java.util.regex.Pattern
import org.codehaus.groovy.ast.expr.ElvisOperatorExpression

@GroovyASTTransformation(phase = CompilePhase.SEMANTIC_ANALYSIS)
@CompileStatic
class AutoImplementAstTransformation implements ASTTransformation {
  private static final Pattern GETTER_PATTERN = ~/\Aget/
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
  private static final ClassNode OPTIONAL_CLASS = makeCached(Optional)
  private static final String CONTEXT = 'context'
  private static final ClassNode CONTEXT_CLASS = makeCached(Context)
  private static final Parameter CONTEXT_PARAM = param(CONTEXT_CLASS, CONTEXT)
  private static final VariableExpression CONTEXT_VAR_X = varX(CONTEXT_PARAM)
  private static final String IMMUTABLE_RAW = 'ImmutableRaw'
  private static final Map<Mutability, String> IMPL = ImmutableMap.of(
    Mutability.MUTABLE, 'Impl',
    Mutability.IMMUTABLE, 'ImmutableImpl',
  )
  private static final ClassNode OBJECT_MAPPER_FACADE = makeCached(ObjectMapperFacade)
  private static final String ABSTRACT_TYPE_MAPPING_REGISTRY = 'ABSTRACT_TYPE_MAPPING_REGISTRY'
  private static final String INTERPOLATE = 'interpolate'
  private static final Expression ABSTRACT_TYPE_MAPPING_REGISTRY_PROP_X = propX(classX(OBJECT_MAPPER_FACADE), constX(ABSTRACT_TYPE_MAPPING_REGISTRY))
  private static final String INTERPOLATE_VALUE = 'interpolateValue'
  private static final String VALUE = 'value'
  private static final ClassNode MUTABILITY_CLASS = makeCached(Mutability)

  @Override
  void visit(ASTNode[] astNodes, SourceUnit sourceUnit) {
    AnnotationNode annotationNode = (AnnotationNode)astNodes[0]
    ClassNode abstractClass = (ClassNode)astNodes[1]
    if (abstractClass.interface || !(abstractClass.modifiers & ACC_ABSTRACT)) {
      addErrorOnAnnotation sourceUnit, annotationNode, 'abstract classes only'
      return
    }
    if (!implementsInterfaceOrSubclassOf(abstractClass, INTERPOLABLE_OBJECT_CLASS)) {
      addErrorOnAnnotation sourceUnit, annotationNode, 'abstract classes implementing InterpolableObject only', abstractClass.interfaces
      return
    }
    if (!getAnnotation(abstractClass, COMPILE_STATIC_CLASS)) {
      addErrorOnAnnotation sourceUnit, annotationNode, 'statically compiled classes only'
      return
    }

    abstractClass.modifiers &= ~ACC_ABSTRACT

    String abstractClassName = abstractClass.nameWithoutPackage
    String abstractClassFullName = abstractClass.name
    Map<Mutability, String> implClassNames = (Map<Mutability, String>)IMPL.collectEntries { Map.Entry<Mutability, String> entry ->
      [(entry.key): "$abstractClassName$entry.value"]
    }
    Map<Mutability, String> implClassFullNames = (Map<Mutability, String>)implClassNames.collectEntries { Map.Entry<Mutability, String> entry ->
      [(entry.key): "$abstractClassFullName$DOLLAR$entry.value"]
    }

    ClassNode abstractClassRef = newClass(abstractClass)

    Map<Mutability, ClassNode> implClasses = (Map<Mutability, ClassNode>)implClassFullNames.collectEntries { Map.Entry<Mutability, String> entry ->
      [(entry.key), new InnerClassNode(
        abstractClassRef,
        entry.value,
        ACC_PUBLIC | ACC_STATIC | ACC_FINAL,
        abstractClassRef,
        EMPTY_CLASS_NODE_ARRAY,
        [] as MixinNode[]
      )]
    }
    Map<Mutability, ClassNode> implClassRefs = (Map<Mutability, ClassNode>)implClasses.collectEntries { Map.Entry<Mutability, ClassNode> entry ->
      [(entry.key), newClass(entry.value)]
    }

    List<Parameter> abstractClassConstructorParams = []
    List<Statement> abstractClassConstructorStmts = []
    List<Expression> implClassDefaultConstructorArgs = []
    List<Parameter> implClassConstructorParams = []
    Map<Mutability, List<Expression>> implClassConstructorArgs = Mutability.values().collectEntries { Mutability mutability ->
      [(mutability): []]
    }
    List<Expression> abstractClassInterpolateConstructorArgs = []

    boolean methodsFound = false

    abstractClass.methods.each { MethodNode method ->
      Matcher m = GETTER_PATTERN.matcher(method.name)
      if (m && method.modifiers & ACC_ABSTRACT && method.parameters.length == 0) { // TODO: + Test for method.code
        methodsFound = true

        method.modifiers &= ~ACC_ABSTRACT
        method.modifiers |= ACC_FINAL

        ClassNode typ = method.returnType

        boolean isValue = implementsInterfaceOrSubclassOf(typ, INTERPOLABLE_VALUE_CLASS)
        ClassNode targetClass
        if (isValue) {
          targetClass = findActualTypeByGenericsPlaceholderName('Target', makeDeclaringAndActualGenericsTypeMap(INTERPOLABLE_VALUE_CLASS, typ))
        }

        String fieldName = m.replaceFirst('').uncapitalize()
        ConstantExpression fieldNameConstant = constX(fieldName)
        Expression thisFieldX = attrX(
          THIS_X,
          fieldNameConstant
        )
        Expression fromFieldX = attrX(
          varX(FROM, abstractClassRef),
          fieldNameConstant
        )
        VariableExpression fieldVarX = varX(fieldName)

        /*
         * Due to https://issues.apache.org/jira/browse/GROOVY-8914 inner classes don't work anyway.
         * Maybe we should remove this code too
         */
        // String typImplClassName =  "$typ.name$DOLLAR${ isValue ? IMMUTABLE_RAW /* TODO: + Mutable version */ : "$typ.name$MUTABLE_IMPL" }"
        // ClassNode typImplType = typ.name.startsWith("$abstractClass.name\$") ? newClass((ClassNode)typ.redirect().innerClasses.find { InnerClassNode innerClassNode -> innerClassNode.name == typImplClassName }) : make(this.class.classLoader.loadClass(typImplClassName))

        AnnotationNode jsonPropertyAnnotation = getAnnotation(method, JSON_PROPERTY_CLASS)
        if (jsonPropertyAnnotation == null && !abstractClass.compileUnit.config.parameters) {
          jsonPropertyAnnotation = new AnnotationNode(JSON_PROPERTY_CLASS)
          jsonPropertyAnnotation.addMember(VALUE, constX(ObjectMapperFacade.PROPERTY_NAMING_STRATEGY.translate(fieldName)))
        }

        AnnotationNode jsonAliasAnnotation = getAnnotation(method, JSON_ALIAS_CLASS)
        if (jsonAliasAnnotation != null) {
          method.annotations.remove(jsonAliasAnnotation) // TODO: add test
        }

        abstractClass.addField(
          fieldName,
          ACC_PRIVATE | ACC_FINAL,
          typ,
          null
        )

        method.code = block(
          stmt(thisFieldX)
        )

        abstractClassConstructorParams.add param(
          typ,
          fieldName
        )

        abstractClassConstructorStmts.add assignS(
          thisFieldX,
          fieldVarX
        )

        implClassDefaultConstructorArgs.add castX(
          typ,
          NULL
        )

        Parameter implClassConstructorParam = param(
          typ,
          fieldName
        )
        if (jsonPropertyAnnotation != null) {
          implClassConstructorParam.addAnnotation(jsonPropertyAnnotation)
        }
        if (jsonAliasAnnotation != null) {
          implClassConstructorParam.addAnnotation(jsonAliasAnnotation)
        }
        implClassConstructorParams.add implClassConstructorParam

        // TODO: + list
        implClassConstructorArgs.each { Map.Entry<Mutability, List<Expression>> entry ->
          entry.value.add new ElvisOperatorExpression(
            fieldVarX,
            callX(
              ABSTRACT_TYPE_MAPPING_REGISTRY_PROP_X,
              'newInstance',
              args(
                classX(typ),
                propX(
                  classX(MUTABILITY_CLASS),
                  entry.key.name()
                )
              )
            )
          )
        }

        // TODO: + Context vars
        AnnotationNode defaultAnnotation = getAnnotation(method, DEFAULT_CLASS)
        AnnotationNode ignoreIfAnnotation = getAnnotation(method, IGNORE_IF_CLASS)
        AnnotationNode postProcessAnnotation = getAnnotation(method, POST_PROCESS_CLASS)

        // TODO: + list
        if (isValue) {
          List<Expression> interpolateValueArgs = [(Expression)CONTEXT_VAR_X]

          if (defaultAnnotation != null) {
            method.annotations.remove(defaultAnnotation)
            if (defaultAnnotation.members['dynamic']) {
              interpolateValueArgs.add defaultAnnotation.members[VALUE]
            } else {
              ClosureExpression defaultClosure = (ClosureExpression)defaultAnnotation.members[VALUE]
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
              interpolateValueArgs.add defaultValueX
            }
          } else if (ignoreIfAnnotation != null || postProcessAnnotation != null) {
            interpolateValueArgs.add castX(
              targetClass,
              NULL
            )
          }

          if (ignoreIfAnnotation != null) {
            method.annotations.remove(ignoreIfAnnotation)
            interpolateValueArgs.add ignoreIfAnnotation.members[VALUE]
            method.addAnnotation(new AnnotationNode(OPTIONAL_CLASS))
          } else if (postProcessAnnotation != null) {
            interpolateValueArgs.add NULL
          }

          if (postProcessAnnotation != null) {
            method.annotations.remove(postProcessAnnotation)
            interpolateValueArgs.add postProcessAnnotation.members[VALUE]
          }

          abstractClassInterpolateConstructorArgs.add callX(
            fromFieldX, INTERPOLATE_VALUE, args(interpolateValueArgs)
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

          abstractClassInterpolateConstructorArgs.add callX(
            fromFieldX,
            INTERPOLATE,
            args(
              CONTEXT_VAR_X
            )
          )
        }
      }
    }

    if (!methodsFound) {
      addErrorOnAnnotation sourceUnit, annotationNode, 'classes that have at least one method to implement'
      return
    }

    abstractClass.addConstructor(
      ACC_PRIVATE,
      abstractClassConstructorParams.toArray(new Parameter[0]),
      EMPTY_CLASS_NODE_ARRAY,
      block(
        null,
        abstractClassConstructorStmts
      ) // TODO: need to call super() ?
    )

    abstractClass.addConstructor(
      ACC_PRIVATE,
      [
        CONTEXT_PARAM,
        param(
          abstractClassRef, FROM
        )
      ].toArray(new Parameter[2]),
      EMPTY_CLASS_NODE_ARRAY,
      block(
        null,
        ctorThisS(
          args(abstractClassInterpolateConstructorArgs)
        )
      ) // TODO: need to call super() ?
    )

    abstractClass.addMethod(
      INTERPOLATE,
      ACC_PUBLIC | ACC_FINAL,
      abstractClassRef,
      [
        CONTEXT_PARAM
      ].toArray(new Parameter[1]),
      EMPTY_CLASS_NODE_ARRAY,
      block(
        returnS(
          ctorX(
            abstractClassRef,
            args(
              CONTEXT_VAR_X,
              THIS_X
            )
          )
        )
      )
    ).addAnnotation(OVERRIDE_ANNOTATION)

    abstractClass.addStaticInitializerStatements([(Statement)block(
      stmt(callX(
        ABSTRACT_TYPE_MAPPING_REGISTRY_PROP_X,
        'registerAbstractTypeMapping',
        args(
          classX(abstractClassRef),
          classX(implClassRefs[Mutability.MUTABLE]),
          classX(implClassRefs[Mutability.IMMUTABLE])
        )
      ))
    )], false)

    implClasses.each { Map.Entry<Mutability, ClassNode> entry ->
      entry.value.addConstructor(
        ACC_PUBLIC,
        EMPTY_PARAMETER_ARRAY,
        EMPTY_CLASS_NODE_ARRAY,
        block(
          null,
          ctorThisS(
            args(implClassDefaultConstructorArgs)
          )
        )
      )

      entry.value.addConstructor(
        ACC_PUBLIC,
        implClassConstructorParams.toArray(new Parameter[0]),
        EMPTY_CLASS_NODE_ARRAY,
        block(
          null,
          ctorSuperS(args(implClassConstructorArgs[entry.key]))
        ) // TODO: need to call super() ?
      ).addAnnotation(new AnnotationNode(JSON_CREATOR_CLASS))

      new StaticCompileTransformation().visit([COMPILE_STATIC_ANNOTATION, entry.value] as ASTNode[], sourceUnit)

      abstractClass.module.addClass(entry.value)
      // sourceUnit.AST.addClass(mutableClass)
      // abstractClass.compileUnit.addClass(mutableClass)
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
