package com.github.hashicorp.packer.engine.ast

import static org.codehaus.groovy.ast.tools.GeneralUtils.cloneParams
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
import static org.codehaus.groovy.ast.tools.GeneralUtils.params
import static org.codehaus.groovy.ast.tools.GeneralUtils.callX
import static org.codehaus.groovy.ast.tools.GeneralUtils.returnS
import static org.codehaus.groovy.ast.ClassHelper.makeCached
import static org.codehaus.groovy.ast.ClassHelper.VOID_TYPE
import static org.codehaus.groovy.ast.tools.WideningCategories.implementsInterfaceOrSubclassOf
import static org.codehaus.groovy.ast.ClassNode.ACC_PUBLIC
import static org.codehaus.groovy.ast.ClassNode.ACC_PROTECTED
import static org.codehaus.groovy.ast.ClassNode.ACC_ABSTRACT
import static org.codehaus.groovy.ast.ClassNode.ACC_PRIVATE
import static org.codehaus.groovy.ast.ClassNode.ACC_STATIC
import static org.codehaus.groovy.ast.ClassNode.ACC_FINAL
import static org.codehaus.groovy.ast.tools.GenericsUtils.newClass
import static org.codehaus.groovy.ast.tools.GenericsUtils.makeDeclaringAndActualGenericsTypeMap
import static org.codehaus.groovy.ast.tools.GenericsUtils.findActualTypeByGenericsPlaceholderName
import static org.codehaus.groovy.ast.ClassNode.EMPTY_ARRAY as EMPTY_CLASS_NODE_ARRAY
import com.fasterxml.jackson.annotation.JacksonInject
import com.fasterxml.jackson.annotation.OptBoolean
import groovy.transform.CompilationUnitAware
import org.codehaus.groovy.ast.ModuleNode
import org.codehaus.groovy.control.CompilationUnit
import com.google.common.collect.ImmutableList
import org.codehaus.groovy.ast.expr.ClassExpression
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.InputDirectory
import org.gradle.api.tasks.InputFile
import org.gradle.api.tasks.InputFiles
import org.gradle.api.tasks.OutputDirectories
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.OutputFile
import org.gradle.api.tasks.OutputFiles
import com.github.hashicorp.packer.engine.Mutability
import com.google.common.collect.ImmutableMap
import com.github.hashicorp.packer.engine.Engine
import org.codehaus.groovy.control.messages.SyntaxErrorMessage
import org.codehaus.groovy.syntax.SyntaxException
import org.codehaus.groovy.ast.AnnotatedNode
import org.codehaus.groovy.transform.sc.StaticCompileTransformation
import com.fasterxml.jackson.annotation.JsonAlias
import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty
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
class AutoImplementAstTransformation implements ASTTransformation, CompilationUnitAware {
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
  private static final Parameter CONTEXT_PARAM = param(
    CONTEXT_CLASS,
    CONTEXT
  )
  private static final VariableExpression CONTEXT_VAR_X = varX(CONTEXT_PARAM)
  private static final String ENGINE = 'engine'
  private static final ClassNode ENGINE_CLASS = makeCached(Engine)
  private static final Parameter ENGINE_PARAM = param(
    ENGINE_CLASS,
    ENGINE
  )
  private static final VariableExpression ENGINE_VAR_X = varX(ENGINE_PARAM)
  private static final Map<Mutability, String> IMPL = ImmutableMap.of(
    Mutability.MUTABLE, 'Impl',
    Mutability.IMMUTABLE, 'ImmutableImpl',
  )
  private static final String INTERPOLATED = 'Interpolated'
  private static final String ABSTRACT_TYPE_MAPPING_REGISTRY = 'abstractTypeMappingRegistry'
  private static final Expression ABSTRACT_TYPE_MAPPING_REGISTRY_PROP_X = propX(
    ENGINE_VAR_X,
    ABSTRACT_TYPE_MAPPING_REGISTRY
  )
  private static final String REGISTER_ABSTRACT_TYPE_MAPPING = 'registerAbstractTypeMapping'
  private static final String INTERPOLATE = 'interpolate'
  private static final String INTERPOLATE_VALUE = 'interpolateValue'
  private static final String VALUE = 'value'
  private static final ClassNode MUTABILITY_CLASS = makeCached(Mutability)
  private static final ClassExpression MUTABILITY_CLASS_X = classX(MUTABILITY_CLASS)
  private static final String DYNAMIC = 'dynamic'
  private static final String NEW_INSTANCE = 'newInstance'
  private static final String TARGET = 'Target'
  private static final int PUBLIC_FINAL = ACC_PUBLIC | ACC_FINAL
  private static final int PRIVATE_FINAL = ACC_PRIVATE | ACC_FINAL
  private static final int PUBLIC_STATIC_FINAL = ACC_PUBLIC | ACC_STATIC | ACC_FINAL
  private static final ClassNode INPUT_CLASS = makeCached(Input)
  private static final ClassNode INPUT_FILE_CLASS = makeCached(InputFile)
  private static final ClassNode INPUT_FILES_CLASS = makeCached(InputFiles)
  private static final ClassNode INPUT_DIRECTORY_CLASS = makeCached(InputDirectory)
  private static final ClassNode OUTPUT_FILE_CLASS = makeCached(OutputFile)
  private static final ClassNode OUTPUT_FILES_CLASS = makeCached(OutputFiles)
  private static final ClassNode OUTPUT_DIRECTORY_CLASS = makeCached(OutputDirectory)
  private static final ClassNode OUTPUT_DIRECTORIES_CLASS = makeCached(OutputDirectories)
  private static final List<ClassNode> GRADLE_TASK_PROPERTIES_ANNOTATION_CLASSES = ImmutableList.of(
    INPUT_CLASS,
    INPUT_FILE_CLASS,
    INPUT_FILES_CLASS,
    INPUT_DIRECTORY_CLASS,
    OUTPUT_FILE_CLASS,
    OUTPUT_FILES_CLASS,
    OUTPUT_DIRECTORY_CLASS,
    OUTPUT_DIRECTORIES_CLASS
  )
  private static final String REGISTER = 'register'
  private static final ClassNode JACKSON_INJECT_CLASS = makeCached(JacksonInject)
  private static final AnnotationNode JACKSON_INJECT_ANNOTATION = new AnnotationNode(JACKSON_INJECT_CLASS)
  static {
    JACKSON_INJECT_ANNOTATION.addMember(
      'useInput',
      propX(
        classX(makeCached(OptBoolean)),
        'FALSE'
      )
    )
  }
  private static final Parameter ENGINE_PARAM_WITH_ANNOTATION = cloneParams(ENGINE_PARAM)[0]
  static {
    ENGINE_PARAM_WITH_ANNOTATION.addAnnotation(JACKSON_INJECT_ANNOTATION)
  }

  @Override
  void visit(ASTNode[] astNodes, SourceUnit source) {
    AnnotationNode autoImplementAnnotationNode = (AnnotationNode)astNodes[0]
    ClassNode abstractClass = (ClassNode)astNodes[1]
    if (abstractClass.interface || !(abstractClass.modifiers & ACC_ABSTRACT)) {
      addErrorOnAnnotation source, autoImplementAnnotationNode, 'abstract classes only'
      return
    }
    if (!implementsInterfaceOrSubclassOf(abstractClass, INTERPOLABLE_OBJECT_CLASS)) {
      addErrorOnAnnotation source, autoImplementAnnotationNode, 'classes implementing InterpolableObject only', abstractClass.interfaces
      return
    }
    if (!getAnnotation(abstractClass, COMPILE_STATIC_CLASS)) {
      addErrorOnAnnotation source, autoImplementAnnotationNode, 'statically compiled types only'
      return
    }

    boolean parameters = abstractClass.compileUnit.config.parameters

    String abstractClassFullName = abstractClass.name
    ClassNode abstractClassRef = newClass(abstractClass)

    Map<Mutability, String> implClassFullNames = (Map<Mutability, String>)IMPL.collectEntries { Map.Entry<Mutability, String> entry ->
      [(entry.key): "$abstractClassFullName$DOLLAR$entry.value"]
    }
    Map<Mutability, ClassNode> implClasses = (Map<Mutability, ClassNode>)implClassFullNames.collectEntries { Map.Entry<Mutability, String> entry ->
      [(entry.key), new InnerClassNode(
        abstractClassRef,
        entry.value,
        PUBLIC_STATIC_FINAL,
        abstractClassRef,
        EMPTY_CLASS_NODE_ARRAY,
        [] as MixinNode[]
      )]
    }
    Map<Mutability, ClassNode> implClassRefs = (Map<Mutability, ClassNode>)implClasses.collectEntries { Map.Entry<Mutability, ClassNode> entry ->
      [(entry.key), newClass(entry.value)]
    }

    ClassNode interpolatedClass = new InnerClassNode(
      abstractClassRef,
      "$abstractClassFullName$DOLLAR$INTERPOLATED",
      PUBLIC_STATIC_FINAL,
      abstractClassRef,
      EMPTY_CLASS_NODE_ARRAY,
      [] as MixinNode[]
    )
    ClassNode interpolatedClassRef = newClass(interpolatedClass)

    List<Parameter> abstractClassCtorParams = []
    List<Statement> abstractClassCtorStmts = []
    List<Expression> implClassDefaultCtorArgs = [(Expression)ENGINE_VAR_X]
    Parameter engineParam = cloneParams(ENGINE_PARAM)[0]
    List<Parameter> implClassCtorParams = [ENGINE_PARAM_WITH_ANNOTATION]
    Map<Mutability, List<Expression>> implClassCtorArgs = Mutability.values().collectEntries { Mutability mutability ->
      [(mutability): []]
    }
    List<Expression> interpolatedClassCtorArgs = []

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
          targetClass = findActualTypeByGenericsPlaceholderName(TARGET, makeDeclaringAndActualGenericsTypeMap(INTERPOLABLE_VALUE_CLASS, typ))
        }

        String fieldName = m.replaceFirst('').uncapitalize()
        ConstantExpression fieldNameConstX = constX(fieldName)
        Expression thisFieldX = attrX(
          THIS_X,
          fieldNameConstX
        )
        Expression fromFieldX = attrX(
          varX(
            FROM,
            abstractClassRef
          ),
          fieldNameConstX
        )
        VariableExpression fieldVarX = varX(fieldName)

        /*
         * Due to https://issues.apache.org/jira/browse/GROOVY-8914 inner classes don't work anyway.
         * Maybe we should remove this code too
         */
        // String typImplClassName =  "$typ.name$DOLLAR${ isValue ? IMMUTABLE_RAW /* TODO: + Mutable version */ : "$typ.name$MUTABLE_IMPL" }"
        // ClassNode typImplType = typ.name.startsWith("$abstractClass.name\$") ? newClass((ClassNode)typ.redirect().innerClasses.find { InnerClassNode innerClassNode -> innerClassNode.name == typImplClassName }) : make(this.class.classLoader.loadClass(typImplClassName))

        AnnotationNode jsonPropertyAnnotation = getAnnotation(method, JSON_PROPERTY_CLASS)
        if (jsonPropertyAnnotation == null && !parameters) {
          jsonPropertyAnnotation = new AnnotationNode(JSON_PROPERTY_CLASS)
          jsonPropertyAnnotation.addMember(VALUE, constX(Engine.PROPERTY_NAMING_STRATEGY.translate(fieldName)))
        }

        AnnotationNode jsonAliasAnnotation = getAnnotation(method, JSON_ALIAS_CLASS)
        if (jsonAliasAnnotation != null) {
          method.annotations.remove(jsonAliasAnnotation) // TODO: add test
        }

        abstractClass.addField(
          fieldName,
          PRIVATE_FINAL,
          typ,
          null
        )

        method.code = block(
          stmt(thisFieldX)
        )

        abstractClassCtorParams.add param(
          typ,
          fieldName
        )

        abstractClassCtorStmts.add assignS(
          thisFieldX,
          fieldVarX
        )

        implClassDefaultCtorArgs.add castX(
          typ,
          NULL
        )

        Parameter implClassCtorParam = param(
          typ,
          fieldName
        )
        if (jsonPropertyAnnotation != null) {
          implClassCtorParam.addAnnotation(jsonPropertyAnnotation)
        }
        if (jsonAliasAnnotation != null) {
          implClassCtorParam.addAnnotation(jsonAliasAnnotation)
        }
        implClassCtorParams.add implClassCtorParam

        // TODO: + list
        implClassCtorArgs.each { Map.Entry<Mutability, List<Expression>> entry ->
          entry.value.add new ElvisOperatorExpression(
            fieldVarX,
            callX(
              ABSTRACT_TYPE_MAPPING_REGISTRY_PROP_X,
              NEW_INSTANCE,
              args(
                classX(typ),
                propX(
                  MUTABILITY_CLASS_X,
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
            if (defaultAnnotation.members[DYNAMIC]) {
              interpolateValueArgs.add defaultAnnotation.members[VALUE]
            } else {
              ClosureExpression defaultClosure = (ClosureExpression)defaultAnnotation.members[VALUE]
              List<Statement> statements = ((BlockStatement)defaultClosure.code).statements
              int countOfStatements = statements.size()
              if (countOfStatements != 1) {
                addErrorOnDefaultClosure source, defaultClosure, 'have exactly one statement', countOfStatements
                return
              }
              Statement firstS = statements.first()
              Expression defaultValueX
              if (ExpressionStatement.isInstance(firstS)) {
                defaultValueX = ((ExpressionStatement)firstS).expression
              } else if (ReturnStatement.isInstance(firstS)) {
                defaultValueX = ((ReturnStatement)firstS).expression
              } else {
                addErrorOnDefaultClosure source, firstS, 'have expression or return statement', firstS, 'If you can\'t express it this way, set dynamic member to true'
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

            Integer lastGradleTaskPropertyAnnotationIndex = null
            boolean hasOptionalAnnotation = false
            method.annotations.eachWithIndex { AnnotationNode annotationNode, Integer i ->
              if (GRADLE_TASK_PROPERTIES_ANNOTATION_CLASSES.any { ClassNode gradleTestPropertyAnnotationClass ->
                implementsInterfaceOrSubclassOf(annotationNode.classNode, gradleTestPropertyAnnotationClass)
              }) {
                lastGradleTaskPropertyAnnotationIndex = i
              } else if (implementsInterfaceOrSubclassOf(annotationNode.classNode, OPTIONAL_CLASS)) {
                hasOptionalAnnotation = true
              }
            }
            if (!hasOptionalAnnotation & lastGradleTaskPropertyAnnotationIndex != null) {
              method.annotations.add lastGradleTaskPropertyAnnotationIndex + 1, new AnnotationNode(OPTIONAL_CLASS)
            }
          } else if (postProcessAnnotation != null) {
            interpolateValueArgs.add NULL
          }

          if (postProcessAnnotation != null) {
            method.annotations.remove(postProcessAnnotation)
            interpolateValueArgs.add postProcessAnnotation.members[VALUE]
          }

          interpolatedClassCtorArgs.add callX(
            fromFieldX,
            INTERPOLATE_VALUE,
            args(interpolateValueArgs)
          )
        } else {
          if (defaultAnnotation != null) {
            addErrorOnValuesOnlyAnnotation source, defaultAnnotation, typ.name
            return
          }
          if (ignoreIfAnnotation != null) {
            addErrorOnValuesOnlyAnnotation source, ignoreIfAnnotation, typ.name
            return
          }
          if (postProcessAnnotation != null) {
            addErrorOnValuesOnlyAnnotation source, postProcessAnnotation, typ.name
            return
          }

          interpolatedClassCtorArgs.add callX(
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
      addErrorOnAnnotation source, autoImplementAnnotationNode, 'classes having at least one method to implement'
      return
    }

    abstractClass.addConstructor(
      ACC_PRIVATE,
      abstractClassCtorParams.toArray(new Parameter[0]),
      EMPTY_CLASS_NODE_ARRAY,
      block(
        null,
        abstractClassCtorStmts
      )
    )

    abstractClass.addMethod(
      INTERPOLATE,
      PUBLIC_FINAL,
      abstractClassRef,
      params(
        CONTEXT_PARAM
      ),
      EMPTY_CLASS_NODE_ARRAY,
      block(
        returnS(
          ctorX(
            interpolatedClassRef,
            args(
              CONTEXT_VAR_X,
              THIS_X
            )
          )
        )
      )
    ).addAnnotation(OVERRIDE_ANNOTATION)

    implClasses.each { Map.Entry<Mutability, ClassNode> entry ->
      entry.value.addConstructor(
        ACC_PUBLIC,
        params(ENGINE_PARAM),
        EMPTY_CLASS_NODE_ARRAY,
        block(
          null,
          ctorThisS(args(implClassDefaultCtorArgs))
        )
      )

      entry.value.addConstructor(
        ACC_PUBLIC,
        implClassCtorParams.toArray(new Parameter[0]),
        EMPTY_CLASS_NODE_ARRAY,
        block(
          null,
          ctorSuperS(args(implClassCtorArgs[entry.key]))
        )
      ).addAnnotation(new AnnotationNode(JSON_CREATOR_CLASS))

      addClass source, abstractClass.module, entry.value
    }

    interpolatedClass.addConstructor(
      ACC_PROTECTED,
      params(
        CONTEXT_PARAM,
        param(
          abstractClassRef,
          FROM
        )
      ),
      EMPTY_CLASS_NODE_ARRAY,
      block(
        null,
        ctorSuperS(
          args(interpolatedClassCtorArgs)
        )
      ) // TODO: need to call super() ?
    )

    addClass source, abstractClass.module, interpolatedClass

    abstractClass.addMethod(
      REGISTER,
      PUBLIC_STATIC_FINAL,
      VOID_TYPE,
      params(ENGINE_PARAM),
      EMPTY_CLASS_NODE_ARRAY,
      block(
        stmt(callX(
          ABSTRACT_TYPE_MAPPING_REGISTRY_PROP_X,
          REGISTER_ABSTRACT_TYPE_MAPPING,
          args(
            classX(abstractClassRef),
            classX(implClassRefs[Mutability.MUTABLE]),
            classX(implClassRefs[Mutability.IMMUTABLE])
          )
        ))
      )
    )
  }

  private void addClass(SourceUnit source, ModuleNode module, ClassNode classNode) {
    staticCompileTransformation.visit([COMPILE_STATIC_ANNOTATION, classNode] as ASTNode[], source)
    module.addClass classNode
    // source.AST.addClass(mutableClass)
    // interfase.compileUnit.addClass(mutableClass)
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

  private final StaticCompileTransformation staticCompileTransformation = new StaticCompileTransformation()

  @Override
  void setCompilationUnit(final CompilationUnit unit) {
    this.@staticCompileTransformation.compilationUnit = unit
  }
}
