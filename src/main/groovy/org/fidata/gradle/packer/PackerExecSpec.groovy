package org.fidata.gradle.packer

import groovy.transform.CompileStatic
import org.gradle.api.Project
import org.ysb33r.grolifant.api.exec.AbstractCommandExecSpec
import org.ysb33r.grolifant.api.exec.ExternalExecutable

@CompileStatic
class PackerExecSpec extends AbstractCommandExecSpec {
  PackerExecSpec(Project project, ExternalExecutable registry) {
    super(project, registry)
    setExecutable('packer')
  }
}
