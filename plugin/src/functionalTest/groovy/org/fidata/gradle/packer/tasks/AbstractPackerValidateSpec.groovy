package org.fidata.gradle.packer.tasks

import groovy.transform.CompileStatic
import org.gradle.api.Project
import org.gradle.testfixtures.ProjectBuilder
import org.junit.Rule
import org.junit.rules.TemporaryFolder
import org.ysb33r.grolifant.api.exec.AbstractCommandExecSpec
import spock.lang.Specification

/**
 * Specification for {@code AbstractPackerValidate} Gradle task class
 */
@CompileStatic
class AbstractPackerValidateSpec extends Specification {
  // fields
  @Rule
  final TemporaryFolder testProjectDir = new TemporaryFolder()

  Project project

  // fixture methods

  // run before the first feature method
  // void setupSpec() { }

  // run before every feature method
  void setup() {
    /*
     * WORKAROUND:
     * https://github.com/tschulte/gradle-semantic-release-plugin/issues/24
     * https://github.com/tschulte/gradle-semantic-release-plugin/issues/25
     * <grv87 2018-06-24>
     */
    project = ProjectBuilder.builder().withProjectDir(testProjectDir.root).build()
  }

  // run after every feature method
  // void cleanup() { }

  // run after the last feature method
  // void cleanupSpec() { }

  // feature methods

  @SuppressWarnings('DuplicateNumberLiteral')
  void 'has correct cmdArgs'() {
    given:
    'base plugin is applied'
    project.apply plugin: 'org.fidata.packer-base'

    when:
    'instance of AbstractPackerValidate task is created'
    File template = new File(testProjectDir.root, 'template.json')
    AbstractPackerValidate packerValidateTemplate = project.tasks.create('packerValidate-template', AbstractPackerValidate) {
      templateFile = template
    }

    then:
    'its cmdArgs has template argument'
    packerValidateTemplate
    packerValidateTemplate.cmdArgs[-1] == template

    and:
    AbstractCommandExecSpec execSpec = packerValidateTemplate.configureExecSpec(packerValidateTemplate.createExecSpec())
    execSpec.command == 'validate'
    execSpec.commandLine[-1] == template.toString()
  }

  // helper methods

}
