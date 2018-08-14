package org.fidata.gradle.packer.tasks

import com.blogspot.toomuchcoding.spock.subjcollabs.Collaborator
import com.blogspot.toomuchcoding.spock.subjcollabs.Subject
import org.gradle.api.Action
import org.gradle.api.Project
import org.gradle.api.internal.GradleInternal
import org.gradle.api.internal.file.DefaultFileOperations
import org.gradle.api.internal.project.DefaultProject
import org.gradle.api.internal.project.ProjectInternal
import org.gradle.internal.service.scopes.ServiceRegistryFactory
import org.gradle.process.ExecSpec
import org.gradle.testfixtures.ProjectBuilder
import org.junit.Rule
import org.junit.rules.TemporaryFolder
import spock.lang.Specification

import javax.annotation.Nullable

class PackerValidateSpec extends Specification {
  // fields
  @Rule
  final TemporaryFolder testProjectDir = new TemporaryFolder()

  @Collaborator
  DefaultFileOperations processOperations = Mock()

  @Subject
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

  void 'has correct cmdArgs'() {
    given:
    'base plugin is applied'
    /*def projectSpy = project // Spy(project)*/
    project.apply plugin: 'org.fidata.packer-base'
    // projectSpy.exec((Closure)_) >> null

    when:
    'instance of PackerValidate task is created'
    File templateFile = new File(testProjectDir.root, 'template.json')
    PackerValidate packerValidateTemplate = project.tasks.create('packerValidate-template', PackerValidate, templateFile) {}

    then:
    'its cmdArgs has template argument'
    packerValidateTemplate.cmdArgs[-1] == templateFile

    when:
    packerValidateTemplate.exec()

    then:
    1 * processOperations.exec({ Action<? super ExecSpec> execSpec ->
      execSpec.commandLine[0] == 'packer' &&
        execSpec.commandLine[1] == 'validate' &&
        execSpec.commandLine[-1] == templateFile.toString()
    })
    noExceptionThrown()
  }

  // helper methods

}
