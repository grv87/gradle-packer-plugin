package org.fidata.gradle.packer.template

import com.fasterxml.uuid.Generators
import com.fasterxml.uuid.NoArgGenerator
import com.samskivert.mustache.Mustache
import groovy.transform.AutoClone
import groovy.transform.AutoCloneStyle
import groovy.transform.CompileStatic
import groovy.transform.EqualsAndHashCode
import org.gradle.api.Task

@EqualsAndHashCode(/*excludes = ['buildName'] TODO*/)
@AutoClone(style = AutoCloneStyle.COPY_CONSTRUCTOR)
@CompileStatic
final class Context {
  final Map<String, String> userVariables

  final Map<String, String> env

  String getBuildName() {
    templateVariables?.get('BuildName')
  }

  final Map<String, ? extends Serializable> templateVariables

  final File templateFile

  final Task task

  private Context(Map<String, String> userVariables, Map<String, String> env, Map<String, ? extends Serializable> templateVariables, File templateFile, Task task) {
    this.userVariables = userVariables.asImmutable()
    this.env = env.asImmutable()
    this.templateVariables = templateVariables.asImmutable()
    this.templateFile = templateFile
    this.task = task
    Map<String, Serializable> aContextTemplateData = [
      'pwd': new File('.').canonicalPath,
      'template_dir': templateFile.parentFile.absolutePath,
      'timestamp': new Date().time.intdiv(1000),
      'uuid': uuidGenerator.generate().toString(),
    ]
    if (userVariables) {
      aContextTemplateData.putAll((Map<String, String>)userVariables.collectEntries { Map.Entry<String, String> entry -> ["user `$entry.key`".toString(), entry.value] })
    }
    if (env) {
      aContextTemplateData.putAll((Map<String, String>)env.collectEntries { Map.Entry<String, String> entry -> ["env `$entry.key`".toString(), entry.value] })
    }
    if (templateVariables) {
      aContextTemplateData.putAll((Map<String, Serializable>)templateVariables.collectEntries { Map.Entry<String, ? extends Serializable> entry -> [".$entry.key".toString(), entry.value] })
    }
    contextTemplateData = aContextTemplateData.asImmutable()
  }

  Context(Map<String, String> userVariables, Map<String, String> env, File templateFile, Task task){
    this(userVariables, env, null, templateFile, task)
  }

  Context addTemplateVariables(Map<String, ? extends Serializable> variables) {
    Map<String, ? extends Serializable> newTemplateVariables = [:]
    if (templateVariables) {
      newTemplateVariables.putAll templateVariables
    }
    newTemplateVariables.putAll variables
    new Context((Map<String, String>)userVariables.clone(), (Map<String, String>)env.clone(), newTemplateVariables, templateFile, task)
  }

  private final Map<String, Serializable> contextTemplateData

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

  static private final NoArgGenerator uuidGenerator = Generators.timeBasedGenerator()
}
