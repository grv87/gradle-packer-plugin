package org.fidata.gradle.packer.template

import com.fasterxml.uuid.Generators
import com.fasterxml.uuid.UUIDGenerator
import com.samskivert.mustache.Mustache
import groovy.transform.CompileStatic
import org.gradle.api.Project
import org.gradle.api.Task

@CompileStatic
final class Context {
  final Map<String, String> userVariables

  final Map<String, String> env

  final Map<String, String> packerVariables

  final String templatePath

  final Task task

  Context(Map<String, String> userVariables, Map<String, String> env, Map<String, String> packerVariables, String templatePath, Task task) {
    this.userVariables = userVariables.asImmutable()
    this.env = env.asImmutable()
    this.packerVariables = userVariables.asImmutable()
    this.templatePath = templatePath
    this.task = task
  }

  Context(Context ctx) {
    new Context((Map<String, String>)ctx.userVariables.clone(), (Map<String, String>)ctx.env.clone(), (Map<String, String>)ctx.packerVariables.clone(), ctx.templatePath, ctx.task)
  }

  Context addPackerVariables(Map<String, String> variables) {
    new Context((Map<String, String>)userVariables.clone(), (Map<String, String>)env.clone(), (Map<String, String>)packerVariables.clone() + variables, templatePath, task)
  }

  private Map<String, String> contextTemplateData

  String interpolateString(String value) {
    /*
     * CAVEAT:
     * Packer uses Go text/template library. It wasn't ported to Java/Groovy
     * This code uses Mustache to parse templates.
     * There could be errors due to slightly different syntax.
     * However, that most probably won't happen in simple templates.
     */
    Mustache.compiler().compile(value).execute(contextTemplateData)
  }

  File interpolateFile(String value) {
    task.project.file(interpolateString(value))
  }

  static private UUIDGenerator uuidGenerator = Generators.timeBasedGenerator()
}
