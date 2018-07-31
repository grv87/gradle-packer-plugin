package org.fidata.gradle.packer

class PackerBuild extends PackerWrapperTask {
  @Override
  protected PackerExecSpec configureExecSpec(PackerExecSpec execSpec) {
    super(execSpec)
    execSpec.command 'build'
    execSpec
  }
}
