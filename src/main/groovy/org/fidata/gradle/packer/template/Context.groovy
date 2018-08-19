package org.fidata.gradle.packer.template

import com.fasterxml.uuid.Generators
import com.fasterxml.uuid.NoArgGenerator
import com.samskivert.mustache.Mustache
import groovy.transform.CompileStatic
import org.gradle.api.Task

@CompileStatic
final class Context {
  final Map<String, String> userVariables

  final Map<String, String> env

  String getBuildName() {
    templateVariables?.get('BuildName')
  }

  final Map<String, String> templateVariables

  final File templateFile

  final Task task

  /*Context(Map<String, String> userVariables, Map<String, String> templateVariables, File templateFile, Task task) {
    Context(userVariables, null, templateVariables, templateFile, task)
  }*/

  /*private*/ Context(Map<String, String> userVariables, Map<String, String> env, Map<String, String> templateVariables, File templateFile, Task task) {
    this.userVariables = userVariables.asImmutable()
    this.env = env.asImmutable()
    this.templateVariables = templateVariables.asImmutable()
    this.templateFile = templateFile
    this.task = task
    contextTemplateData = [
      'pwd': new File('.').getCanonicalPath(),
      'template_dir': templateFile.getParentFile().getAbsolutePath(),
      'timestamp': new Date().time.intdiv(1000).toString(),
      'uuid': uuidGenerator.generate().toString(),
    ]
    if (userVariables) {
      contextTemplateData += (Map<String, String>)userVariables.collectEntries { Map.Entry<String, String> entry -> ["user `$entry.key`".toString(), entry.value] }
    }
    if (env) {
      contextTemplateData += (Map<String, String>)env.collectEntries { Map.Entry<String, String> entry -> ["env `$entry.key`".toString(), entry.value] }
    }
    if (templateVariables) {
      contextTemplateData += (Map<String, String>)templateVariables.collectEntries { Map.Entry<String, String> entry -> [".$entry.key".toString(), entry.value] }
    }
    contextTemplateData = contextTemplateData.asImmutable()
  }

  Context(Context context) {
    new Context((Map<String, String>)context.userVariables.clone(), (Map<String, String>)context.env.clone(), (Map<String, String>)context.templateVariables.clone(), context.templateFile, context.task)
  }

  Context addTemplateVariables(Map<String, String> variables) {
    if (templateVariables) {
      variables += (Map<String, String>)templateVariables.clone()
    }
    new Context((Map<String, String>)userVariables.clone(), (Map<String, String>)env.clone(), variables, templateFile, task)
  }

  private Map<String, String> contextTemplateData = [:]

  String interpolateString(String value) {
    /*
     * WORKAROUND:
     * Packer uses Go text/template library. It wasn't ported to Java/Groovy
     * This code uses Mustache to parse templates.
     * There could be errors due to slightly different syntax.
     * However, that most probably won't happen in simple templates.
     * <grv87 2018-08-19>
     */
    Mustache.compiler().compile(value).execute(contextTemplateData)
  }

  File interpolateFile(String value) {
    task.project.file(interpolateString(value))
  }

  static private NoArgGenerator uuidGenerator = Generators.timeBasedGenerator()

  @Override
  boolean equals(Object obj) {
    this.class.isInstance(obj) &&
      userVariables == ((Context)obj).userVariables &&
      env == ((Context)obj).env &&
      templateVariables == ((Context)obj).templateVariables &&
      templateFile == ((Context)obj).templateFile &&
      task == ((Context)obj).task
  }

  @Override
  int hashCode() {
    int result = 17
    for (int c : [
      userVariables.hashCode() ?: 0,
      env.hashCode() ?: 0,
      templateVariables.hashCode() ?: 0,
      templateFile.hashCode() ?: 0,
      task.hashCode() ?: 0,
    ]) {
      result = 31 * result + c
    }
    result
 }
}
