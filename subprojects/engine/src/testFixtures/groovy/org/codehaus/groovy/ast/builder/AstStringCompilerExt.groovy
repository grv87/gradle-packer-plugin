/*
 *  Licensed to the Apache Software Foundation (ASF) under one
 *  or more contributor license agreements.  See the NOTICE file
 *  distributed with this work for additional information
 *  regarding copyright ownership.  The ASF licenses this file
 *  to you under the Apache License, Version 2.0 (the
 *  "License"); you may not use this file except in compliance
 *  with the License.  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing,
 *  software distributed under the License is distributed on an
 *  "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *  KIND, either express or implied.  See the License for the
 *  specific language governing permissions and limitations
 *  under the License.
 */
package org.codehaus.groovy.ast.builder

import groovy.transform.CompileStatic
import org.codehaus.groovy.ast.ASTNode
import org.codehaus.groovy.ast.ModuleNode
import org.codehaus.groovy.control.CompilationUnit
import org.codehaus.groovy.control.CompilePhase
import org.codehaus.groovy.control.CompilerConfiguration
import java.security.AccessController
import java.security.PrivilegedAction

/**
 * This class handles converting Strings to ASTNode lists.
 * This is extended version of {@link AstStringCompiler} that accepts
 * custom compiler configuration
 *
 * @author Hamlet D'Arcy
 */
@CompileStatic
class AstStringCompilerExt {

  /**
   * Performs the String source to {@link List} of {@link ASTNode}.
   *
   * @param script
   *      a Groovy script in String form
   * @param compilePhase
   *      the int based CompilePhase to compile it to.
   * @param statementsOnly
   *      when true, only the script statements are returned. When false, you will
   *      receive back a Script class node also. Default is true.
   * @param compilerConfiguration Compiler configuration to use
   */
  static List<ASTNode> compile(String script, CompilePhase compilePhase, boolean statementsOnly = true, CompilerConfiguration compilerConfiguration) {
    final scriptClassName = makeScriptClassName()
    GroovyCodeSource codeSource = new GroovyCodeSource(script, "${scriptClassName}.groovy", "/groovy/script")
    CompilationUnit cu = new CompilationUnit(compilerConfiguration, codeSource.codeSource, AccessController.doPrivileged({ new GroovyClassLoader() } as PrivilegedAction<GroovyClassLoader>))
    cu.addSource(codeSource.name, script)
    cu.compile(compilePhase.phaseNumber)
    // collect all the ASTNodes into the result, possibly ignoring the script body if desired
    return (List<ASTNode>) cu.AST.modules.inject([]) { List acc, ModuleNode node ->
      if (node.statementBlock) acc.add(node.statementBlock)
      node.classes?.each {
        if (!(statementsOnly && it.name == scriptClassName)) {
          acc << it
        }
      }
      acc
    }
  }

  private static String makeScriptClassName() {
    return "Script${System.nanoTime()}"
  }
}
