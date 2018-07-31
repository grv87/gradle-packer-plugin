package org.fidata.gradle.packer

import groovy.transform.CompileStatic
import org.gradle.api.Project
import org.gradle.api.Task
import org.ysb33r.grolifant.api.exec.AbstractToolExtension

@CompileStatic
class PackerToolExtension extends AbstractToolExtension {
  static final String NAME = 'toolConfig'

  PackerToolExtension(Project project) {
    super(project)
  }

  PackerToolExtension(Task task) {
    super(task, NAME)
  }

}
