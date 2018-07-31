package org.fidata.gradle.packer

import groovy.transform.CompileStatic
import org.ysb33r.grolifant.api.exec.AbstractExecWrapperTask

@CompileStatic
class PackerWrapperTask extends AbstractExecWrapperTask<PackerExecSpec, PackerToolExtension> {
  PackerWrapperTask() {
    super()
    packerToolExtension = extensions.create(PackerToolExtension.NAME, PackerToolExtension, this)
  }

  @Override
  protected PackerExecSpec createExecSpec() {
    new PackerExecSpec(project, getToolExtension().getResolver())
  }

  @Override
  protected PackerExecSpec configureExecSpec(PackerExecSpec execSpec) {
    execSpec.cmdArgs '--yellow', '--bright' // TODO
    execSpec
  }


  @Override
  protected PackerToolExtension getToolExtension() {
    this.packerToolExtension
  }

  private PackerToolExtension packerToolExtension
}
