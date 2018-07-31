package org.fidata.gradle.packer

class PackerValidate extends PackerWrapperTask {
  @Override
  protected PackerExecSpec configureExecSpec(PackerExecSpec execSpec) {
    super(execSpec)
    execSpec.command 'validate'
    execSpec
  }
}
