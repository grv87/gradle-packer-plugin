/*
 * AstAssert class
 * This file is copied from Groovy 2.5.4 source under Apache 2.0 license.
 * Copyright © 2018  Basil Peace
 * Copyright 2003-2018 The Apache Software Foundation
 *
 * This file is part of gradle-packer-plugin.
 *
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
/*
 *  The following changes were made to the original:
 *  * Properties referencing ClassNode are compared by type name only
 *  * Added support for ModuleNode, CompareToNullExpression and OptimizingBooleanExpression types
 *  * Added path argument so that failed assertion reports what exactly is different
 *  * Indentation changed to two spaces
 */
package org.codehaus.groovy.ast.builder

import org.junit.Assert

/**
 *
 * Some useful AST assertion methods.
 * @author Hamlet D'Arcy
 */
class AstAssert {
  /**
   * Support for new assertion types can be added by adding a Map<String, Closure> entry.
   */
  private static Map<Object, Closure> ASSERTION_MAP = [
    BlockStatement : { expected, actual, path ->
      assertSyntaxTree(expected.statements, actual.statements, "${ path }.statements")
    },
    AttributeExpression : { expected, actual, path ->
      assertSyntaxTree([expected.objectExpression], [actual.objectExpression], "${ path }.objectExpression")
      assertSyntaxTree([expected.property], [actual.property], "${ path }.property")
    },
    ExpressionStatement : { expected, actual, path ->
      assertSyntaxTree([expected.expression], [actual.expression], "${ path }.expression")
    },
    BitwiseNegationExpression : { expected, actual, path ->
      assertSyntaxTree([expected.expression], [actual.expression], "${ path }.expression")
    },
    CastExpression : { expected, actual, path ->
      assertSyntaxTree([expected.expression], [actual.expression], "${ path }.expression")
      assertNameOnly expected, actual, path, 'type'
    },
    ClosureExpression : { expected, actual, path ->
      assertSyntaxTree(expected.parameters, actual.parameters, "${ path }.parameters")
      assertSyntaxTree([expected.code], [actual.code], "${ path }.code")
    },
    ConstantExpression : { expected, actual, path ->
      Assert.assertEquals("$path: Wrong constant", expected.value, actual.value)
    },
    ArrayExpression : { expected, actual, path ->
      assertNameOnly expected, actual, path, 'elementType' // TODO: Here was a bug in Groovy
      assertSyntaxTree(expected.expressions, actual.expressions, "${ path }.expressions")
    },
    ListExpression : { expected, actual, path ->
      assertSyntaxTree(expected.expressions, actual.expressions, "${ path }.expressions")
    },
    DeclarationExpression : { expected, actual, path ->
      Assert.assertEquals("$path: Wrong token", expected.operation.text, actual.operation.text)
      assertSyntaxTree([expected.leftExpression], [actual.leftExpression], "${ path }.leftExpression")
      assertSyntaxTree([expected.rightExpression], [actual.rightExpression], "${ path }.rightExpression")
    },
    VariableExpression : { expected, actual, path ->
      Assert.assertEquals("$path: Wrong variable", expected.variable, actual.variable)
    },
    ReturnStatement : { expected, actual, path ->
      assertSyntaxTree([expected.expression], [actual.expression], "${ path }.expression")
    },
    ArgumentListExpression : { expected, actual, path ->
      assertSyntaxTree(expected.expressions, actual.expressions, "${ path }.expressions")
    },
    AnnotationConstantExpression : { expected, actual, path ->
      assertSyntaxTree([expected.value], [actual.value], "${ path }.value")
    },
    MethodCallExpression : { expected, actual, path ->
      assertSyntaxTree([expected.objectExpression], [actual.objectExpression], "${ path }.objectExpression")
      assertSyntaxTree([expected.method], [actual.method], "${ path }.method")
      assertSyntaxTree([expected.arguments], [actual.arguments], "${ path }.argument")
    },
    AnnotationNode : { expected, actual, path ->
      // assertSyntaxTree([expected.classNode], [actual.classNode], "${ path }.classNode")
      assertNameOnly expected, actual, path, 'classNode'

      Assert.assertEquals("$path: Wrong members keyset", expected.members.keySet(), actual.members.keySet())
      expected.members.each { key, value ->
        assertSyntaxTree([value], [actual.members[key]], "${ path }.members[$key]")
      }
    },
    ClassNode : { expected, actual, path ->
      assertNameOnly expected, actual, path, 'outerClass'
      assertNameOnly expected, actual, path, 'superClass'
      assertNamesOnly expected, actual, path, 'interfaces'
      assertNamesOnly expected, actual, path, 'mixins'
      assertSyntaxTree(expected.annotations, actual.annotations, "${ path }.annotations")
      assertSyntaxTree(expected.genericsTypes, actual.genericsTypes, "${ path }.genericsTypes")
      assertSyntaxTree(expected.fields, actual.fields, "${ path }.fields")
      assertSyntaxTree(expected.declaredConstructors, actual.declaredConstructors, "${ path }.declaredConstructors")
      assertSyntaxTree(expected.methods, actual.methods, "${ path }.methods")
      assertSyntaxTree(expected.objectInitializerStatements, actual.objectInitializerStatements, "${ path }.objectInitializerStatements")
    },
    IfStatement : { expected, actual, path ->
      assertSyntaxTree([expected.booleanExpression], [actual.booleanExpression], "${ path }.booleanExpression")
      assertSyntaxTree([expected.ifBlock], [actual.ifBlock], "${ path }.ifBlock")
      assertSyntaxTree([expected.elseBlock], [actual.elseBlock], "${ path }.elseBlock")
    },
    BooleanExpression : { expected, actual, path ->
      assertSyntaxTree([expected.expression], [actual.expression], "${ path }.expression")
    },
    BinaryExpression : { expected, actual, path ->
      assertSyntaxTree([expected.leftExpression], [actual.leftExpression], "${ path }.leftExpression")
      assertSyntaxTree([expected.rightExpression], [actual.rightExpression], "${ path }.rightExpression")
      assertSyntaxTree([expected.operation], [actual.operation], "${ path }.operation")
    },
    Token : { expected, actual, path ->
      Assert.assertEquals("$path: Wrong token type", expected.type, actual.type)
      Assert.assertEquals("$path: Wrong token text", expected.text, actual.text)
    },
    Parameter : { expected, actual, path ->
      assertNameOnly expected, actual, path, 'type'
      assertSyntaxTree([expected.defaultValue], [actual.defaultValue], "${ path }.defaultValue")
      Assert.assertEquals("$path: Wrong parameter name", expected.name, actual.name)
      assertSyntaxTree(expected.annotations, actual.annotations, "${ path }.annotations")
    },
    ConstructorCallExpression : { expected, actual, path ->
      assertNameOnly expected, actual, path, 'type'
      assertSyntaxTree([expected.arguments], [actual.arguments], "${ path }.arguments")
    },
    NotExpression : { expected, actual, path ->
      assertSyntaxTree([expected.expression], [actual.expression], "${ path }.expression")
    },
    PostfixExpression : { expected, actual, path ->
      assertSyntaxTree([expected.expression], [actual.expression], "${ path }.expression")
      assertSyntaxTree([expected.operation], [actual.operation], "${ path }.operation")
    },
    PrefixExpression : { expected, actual, path ->
      assertSyntaxTree([expected.expression], [actual.expression], "${ path }.expression")
      assertSyntaxTree([expected.operation], [actual.operation], "${ path }.operation")
    },
    UnaryPlusExpression : { expected, actual, path ->
      assertSyntaxTree([expected.expression], [actual.expression], "${ path }.expression")
    },
    UnaryMinusExpression : { expected, actual, path ->
      assertSyntaxTree([expected.expression], [actual.expression], "${ path }.expression")
    },
    ClassExpression : { expected, actual, path ->
      assertNameOnly expected, actual, path, 'type'
    },
    TupleExpression : { expected, actual, path ->
      assertNameOnly expected, actual, path, 'type'
      assertSyntaxTree(expected.expressions, actual.expressions, "${ path }.expressions")
    },
    FieldExpression : { expected, actual, path ->
      assertSyntaxTree([expected.field], [actual.field], "${ path }.field")
    },
    FieldNode : { expected, actual, path ->
      Assert.assertEquals("$path: Wrong name", expected.name, actual.name)
      assertNameOnly expected, actual, path, 'owner'
      Assert.assertEquals("$path: Wrong modifiers", expected.modifiers, actual.modifiers)
      assertNameOnly expected, actual, path, 'type'
      assertSyntaxTree([expected.initialValueExpression], [actual.initialValueExpression], "${ path }.initialValueExpression")
      assertSyntaxTree(expected.annotations, actual.annotations, "${ path }.annotations")
    },
    MapExpression : { expected, actual, path ->
      assertSyntaxTree(expected.mapEntryExpressions, actual.mapEntryExpressions, "${ path }.mapEntryExpressions")
    },
    MapEntryExpression : { expected, actual, path ->
      assertSyntaxTree([expected.keyExpression], [actual.keyExpression], "${ path }.keyExpression")
      assertSyntaxTree([expected.valueExpression], [actual.valueExpression], "${ path }.valueExpression")
    },
    GStringExpression : { expected, actual, path ->
      Assert.assertEquals("$path: Wrong text", expected.verbatimText, actual.verbatimText)
      assertSyntaxTree(expected.strings, actual.strings, "${ path }.strings")
      assertSyntaxTree(expected.values, actual.values, "${ path }.values")
    },
    MethodPointerExpression : { expected, actual, path ->
      assertSyntaxTree([expected.expression], [actual.expression], "${ path }.expression")
      assertSyntaxTree([expected.methodName], [actual.methodName], "${ path }.methodName")
    },
    RangeExpression : { expected, actual, path ->
      assertSyntaxTree([expected.from], [actual.from], "${ path }.from")
      assertSyntaxTree([expected.to], [actual.to], "${ path }.to")
      Assert.assertEquals("$path: Wrong inclusive", expected.inclusive, actual.inclusive)
    },
    PropertyExpression : { expected, actual, path ->
      assertSyntaxTree([expected.objectExpression], [actual.objectExpression], "${ path }.objectExpression")
      assertSyntaxTree([expected.property], [actual.property], "${ path }.property")
    },
    SwitchStatement : { expected, actual, path ->
      assertSyntaxTree([expected.expression], [actual.expression], "${ path }.expression")
      assertSyntaxTree([expected.defaultStatement], [actual.defaultStatement], "${ path }.defaultStatement")
      assertSyntaxTree(expected.caseStatements, actual.caseStatements, "${ path }.caseStatements")
    },
    CaseStatement : { expected, actual, path ->
      assertSyntaxTree([expected.expression], [actual.expression], "${ path }.expression")
      assertSyntaxTree([expected.code], [actual.code], "${ path }.code")
    },
    EmptyStatement : { expected, actual, path ->
      // always successful
    },
    BreakStatement : { expected, actual, path ->
      Assert.assertEquals("$path: Wrong label", expected.label, actual.label)
    },
    AssertStatement : { expected, actual, path ->
      assertSyntaxTree([expected.booleanExpression], [actual.booleanExpression], "${ path }.booleanExpression")
      assertSyntaxTree([expected.messageExpression], [actual.messageExpression], "${ path }.messageExpression")
    },
    SynchronizedStatement : { expected, actual, path ->
      assertSyntaxTree([expected.expression], [actual.expression], "${ path }.expression")
      assertSyntaxTree([expected.code], [actual.code], "${ path }.code")
    },
    TryCatchStatement : { expected, actual, path ->
      assertSyntaxTree([expected.tryStatement], [actual.tryStatement], "${ path }.tryStatement")
      assertSyntaxTree(expected.catchStatements, actual.catchStatements, "${ path }.catchStatements")
      assertSyntaxTree([expected.finallyStatement], [actual.finallyStatement], "${ path }.finallyStatement")
    },
    CatchStatement : { expected, actual, path ->
      assertSyntaxTree([expected.variable], [actual.variable], "${ path }.variable")
      assertSyntaxTree([expected.code], [actual.code], "${ path }.code")
    },
    ThrowStatement : { expected, actual, path ->
      assertSyntaxTree([expected.expression], [actual.expression], "${ path }.expression")
    },
    StaticMethodCallExpression : { expected, actual, path ->
      assertNameOnly expected, actual, path, 'ownerType'
      Assert.assertEquals("$path: Wrong method", expected.method, actual.method)
      assertSyntaxTree([expected.arguments], [actual.arguments], "${ path }.arguments")
    },
    ForStatement : { expected, actual, path ->
      assertSyntaxTree([expected.variable], [actual.variable], "${ path }.variable")
      assertSyntaxTree([expected.collectionExpression], [actual.collectionExpression], "${ path }.collectionExpression")
      assertSyntaxTree([expected.loopBlock], [actual.loopBlock], "${ path }.loopBlock")
    },
    ClosureListExpression : { expected, actual, path ->
      assertSyntaxTree(expected.expressions, actual.expressions, "${ path }.expressions")
    },
    WhileStatement : { expected, actual, path ->
      assertSyntaxTree([expected.booleanExpression], [actual.booleanExpression], "${ path }.booleanExpression")
      assertSyntaxTree([expected.loopBlock], [actual.loopBlock], "${ path }.loopBlock")
    },
    ContinueStatement : { expected, actual, path ->
      Assert.assertEquals("$path: Wrong label", expected.label, actual.label)
    },
    TernaryExpression : { expected, actual, path ->
      assertSyntaxTree([expected.booleanExpression], [actual.booleanExpression], "${ path }.booleanExpression")
      assertSyntaxTree([expected.trueExpression], [actual.trueExpression], "${ path }.trueExpression")
      assertSyntaxTree([expected.falseExpression], [actual.falseExpression], "${ path }.falseExpression")
    },
    ElvisOperatorExpression : { expected, actual, path ->
      assertSyntaxTree([expected.booleanExpression], [actual.booleanExpression], "${ path }.booleanExpression")
      assertSyntaxTree([expected.trueExpression], [actual.trueExpression], "${ path }.trueExpression")
      assertSyntaxTree([expected.falseExpression], [actual.falseExpression], "${ path }.falseExpression")
    },
    PropertyNode : { expected, actual, path ->
      Assert.assertEquals("$path: Wrong name", expected.name, actual.name)
      Assert.assertEquals("$path: Wrong modifiers", expected.modifiers, actual.modifiers)
      assertSyntaxTree(expected.annotations, actual.annotations, "${ path }.annotations")
      assertSyntaxTree([expected.field], [actual.field], "${ path }.field")
      assertSyntaxTree([expected.getterBlock], [actual.getterBlock], "${ path }.getterBlock")
      assertSyntaxTree([expected.setterBlock], [actual.setterBlock], "${ path }.setterBlock")
    },
    NullObject : { expected, actual, path ->
      Assert.assertNull(expected)
      Assert.assertNull(actual)
    },
    MethodNode : { expected, actual, path ->
      assertNameOnly expected, actual, path, 'declaringClass'
      Assert.assertEquals("$path: Wrong name", expected.name, actual.name)
      Assert.assertEquals("$path: Wrong modifiers", expected.modifiers, actual.modifiers)
      assertSyntaxTree(expected.annotations, actual.annotations, "${ path }.annotations")
      assertSyntaxTree(expected.parameters, actual.parameters, "${ path }.parameters")
      assertNamesOnly expected, actual, path, 'exceptions'
      assertNameOnly expected, actual, path, 'returnType'
      assertSyntaxTree([expected.code], [actual.code], "${ path }.code")
    },
    ConstructorNode : { expected, actual, path ->
      assertNameOnly expected, actual, path, 'declaringClass'
      Assert.assertEquals("$path: Wrong name", expected.name, actual.name)
      Assert.assertEquals("$path: Wrong modifiers", expected.modifiers, actual.modifiers)
      assertSyntaxTree(expected.annotations, actual.annotations, "${ path }.annotations")
      assertSyntaxTree(expected.parameters, actual.parameters, "${ path }.parameters")
      assertNamesOnly expected, actual, path, 'exceptions'
      assertSyntaxTree([expected.code], [actual.code], "${ path }.code")
    },
    ImportNode : { expected, actual, path ->
      assertNameOnly expected, actual, path, 'type'
      Assert.assertEquals("$path: Wrong alias", expected.alias, actual.alias)
      assertSyntaxTree(expected.annotations, actual.annotations, "${ path }.annotations")
    },
    RegexExpression : { expected, actual, path ->
      assertNameOnly expected, actual, path, 'type'
      assertSyntaxTree([expected.string], [actual.string], "${ path }.string")
    },
    SpreadExpression : { expected, actual, path ->
      assertNameOnly expected, actual, path, 'type'
      assertSyntaxTree([expected.expression], [actual.expression], "${ path }.expression")
    },
    SpreadMapExpression : { expected, actual, path ->
      assertNameOnly expected, actual, path, 'type'
      assertSyntaxTree([expected.expression], [actual.expression], "${ path }.expression")
    },
    GenericsType : { expected, actual, path ->
      assertNameOnly expected, actual, path, 'type'
      assertNameOnly expected, actual, path, 'lowerBound'
      assertNamesOnly expected, actual, path, 'upperBounds'
      Assert.assertEquals("$path: Wrong wildcard", expected.name, actual.name, "${ path }.name")
      Assert.assertEquals("$path: Wrong wildcard", expected.wildcard, actual.wildcard, "${ path }.wildcard")
    },
    NamedArgumentListExpression : { expected, actual, path ->
      assertSyntaxTree(expected.mapEntryExpressions, actual.mapEntryExpressions, "${ path }.mapEntryExpressions")
    },
    MixinNode : { expected, actual, path ->
      Assert.assertEquals("$path: Wrong name", expected.name, actual.name)
      Assert.assertEquals("$path: Wrong modifiers", expected.modifiers, actual.modifiers)
      assertNameOnly expected, actual, path, 'outerClass'
      assertNameOnly expected, actual, path, 'superClass'
      assertNamesOnly expected, actual, path, 'interfaces'
      assertNamesOnly expected, actual, path, 'mixins'
      assertSyntaxTree(expected.annotations, actual.annotations, "${ path }.annotations")
      assertSyntaxTree(expected.genericsTypes, actual.genericsTypes, "${ path }.genericsTypes")
      assertSyntaxTree(expected.fields, actual.fields, "${ path }.fields")
      assertSyntaxTree(expected.declaredConstructors, actual.declaredConstructors, "${ path }.declaredConstructors")
      assertSyntaxTree(expected.methods, actual.methods, "${ path }.methods")
      assertSyntaxTree(expected.objectInitializerStatements, actual.objectInitializerStatements, "${ path }.objectInitializerStatements")
    },
    InnerClassNode : { expected, actual, path ->
      Assert.assertEquals("$path: Wrong name", expected.name, actual.name)
      Assert.assertEquals("$path: Wrong modifiers", expected.modifiers, actual.modifiers)
      assertNameOnly expected, actual, path, 'outerClass'
      assertNameOnly expected, actual, path, 'superClass'
      assertNamesOnly expected, actual, path, 'interfaces'
      assertNamesOnly expected, actual, path, 'mixins'
      assertSyntaxTree(expected.annotations, actual.annotations, "${ path }.annotations")
      assertSyntaxTree(expected.genericsTypes, actual.genericsTypes, "${ path }.genericsTypes")
      assertSyntaxTree(expected.fields, actual.fields, "${ path }.fields")
      assertSyntaxTree(expected.declaredConstructors, actual.declaredConstructors, "${ path }.declaredConstructors")
      assertSyntaxTree(expected.methods, actual.methods, "${ path }.methods")
      assertSyntaxTree(expected.objectInitializerStatements, actual.objectInitializerStatements, "${ path }.objectInitializerStatements")
    },
    ModuleNode: { expected, actual, path ->
      assertNameOnly expected, actual, path, 'package'

      Assert.assertEquals("$path: Wrong staticStarImports keyset", expected.staticStarImports.keySet(), actual.staticStarImports.keySet())
      expected.staticStarImports.each { key, value ->
        assertSyntaxTree([value], [actual.staticStarImports[key]], "${ path }.staticStarImports[$key]")
      }

      Assert.assertEquals("$path: Wrong staticImports keyset", expected.staticImports.keySet(), actual.staticImports.keySet())
      expected.staticImports.each { key, value ->
        assertSyntaxTree([value], [actual.staticImports[key]], "${ path }.staticImports[$key]")
      }

      assertSyntaxTree(expected.starImports, actual.starImports, "${ path }.starImports")
      assertSyntaxTree(expected.imports, actual.imports, "${ path }.imports")
      assertSyntaxTree(expected.classes, actual.classes, "${ path }.classes")
      assertSyntaxTree(expected.methods, actual.methods, "${ path }.methods")
      assertSyntaxTree([expected.statementBlock], [actual.statementBlock], "${ path }.statementBlock")
    },
    CompareToNullExpression : { expected, actual, path ->
      assertSyntaxTree([expected.objectExpression], [actual.objectExpression], "${ path }.objectExpression")
      assertSyntaxTree([expected.operation], [actual.operation], "${ path }.operation")
    },
    OptimizingBooleanExpression : { expected, actual, path ->
      assertSyntaxTree([expected.expression], [actual.expression], "${ path }.expression")
    },
  ]

  /**
   * Assertion statement to compare abstract syntax trees.
   * @param expected
   *      the list or array of ASTNodes expected to be present
   * @param actual
   *      the actual list or array of ASTNodes received
   */
  static void assertSyntaxTree(expected, actual) {
    assertSyntaxTree(expected, actual, '')
  }

  /**
   * Assertion statement to compare abstract syntax trees.
   * @param expected
   *      the list or array of ASTNodes expected to be present
   * @param actual
   *      the actual list or array of ASTNodes received
   * @param path
   *      path to AST nodes, will be used in message on failed assertion
   */
  static void assertSyntaxTree(expected, actual, path) {
    if (expected == null && actual == null) return

    if (actual == null || expected == null || expected?.size() != actual?.size()) {
      Assert.fail("$path: Wrong # items. \nExpected $expected \nReceived $actual")
    }
    expected.eachWithIndex { expectedItem, index ->
      def actualItem = actual[index]
      def itemPath = "$path[$index]"
      if (expectedItem != null || actualItem != null) {
        if (expectedItem == null || actualItem == null) {
          Assert.fail("$itemPath: Wrong item. \nExpected $expectedItem \nReceived $actualItem")
        }
        def expectedClass = expectedItem.class
        if (expectedClass.class && actualItem.class.isArray()) {
          assertSyntaxTree(expectedItem, actualItem, itemPath)
        } else {
          Assert.assertEquals("$itemPath: Wrong type in AST Node", expectedClass, actualItem.class)

          if (ASSERTION_MAP.containsKey(expectedClass.simpleName)) {
            Closure assertion = ASSERTION_MAP.get(expectedClass.simpleName)
            assertion(expectedItem, actualItem, itemPath)
          } else {
            Assert.fail("$itemPath: Unexpected type: ${ expectedClass } Update the unit test!")
          }
        }
      }
    }
  }

  private static void assertProperty(expected, actual, path, propertyName) {
    // TODO
    assertSyntaxTree([expected."$propertyName"], [actual."$propertyName"], "$path.$propertyName")
  }

  private static void assertArrayProperty(expected, actual, path, propertyName) {
    // TODO
    assertSyntaxTree(expected."$propertyName", actual."$propertyName", "$path.$propertyName")
  }

  private static void assertNameOnly(expected, actual, path, propertyName) {
    def expectedItem = expected."$propertyName"
    def actualItem = actual."$propertyName"

    def itemPath = "$path.$propertyName"

    if (expectedItem == null && actualItem == null) return

    if (expectedItem == null || actualItem == null) {
      Assert.fail("$itemPath: Wrong ${ propertyName }. \nExpected $expectedItem \nReceived $actualItem")
    }
    Assert.assertEquals("$itemPath: Wrong ${ propertyName }.name", expectedItem.name, actualItem.name)
  }

  private static void assertNamesOnly(expected, actual, path, propertyName) {
    def expectedItems = expected."$propertyName"
    def actualItems = actual."$propertyName"

    if (expectedItems == null && actualItems == null) return

    def itemsPath = "$path.$propertyName"

    if (expectedItems == null || actualItems == null || expectedItems?.size() != actualItems?.size()) {
      Assert.fail("$path: Wrong # $propertyName. \nExpected ${ expectedItems*.name } \nReceived ${ actualItems*.name }")
    }
    expectedItems.eachWithIndex { expectedItem, index ->

      def actualItem = actualItems[index]

      def itemPath = "$itemsPath[$index]"

      if (expectedItem == null && actualItem == null) return

      if (expectedItem == null || actualItem == null) {
        Assert.fail("$itemPath: Wrong item. \nExpected $expectedItem \nReceived $actualItem")
      }
      Assert.assertEquals("$itemPath: Wrong name", expectedItem.name, actualItem.name)
    }
  }
}
