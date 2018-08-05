package org.fidata.gradle.packer.template

import com.samskivert.mustache.Mustache
import groovy.transform.CompileStatic
import org.gradle.api.Project

@CompileStatic
class Context {
  Map<String, String> userVariables

  Map<String, String> env

  String buildName

  String buildType

  String templatePath

  Project project

  String interpolateString(String value) {
    /*
     * CAVEAT:
     * Packer uses Go text/template library. It wasn't ported to Java/Groovy
     * This code uses Mustache to parse templates.
     * There could be errors due to slightly different syntax.
     * However, that most probably won't happen in simple templates.
     */
    Mustache.compiler().compile(value).execute(contextTemplateData) // TODO
  }

  File interpolateFile(String value) {
    project.file(interpolateString(value))
  }
}
