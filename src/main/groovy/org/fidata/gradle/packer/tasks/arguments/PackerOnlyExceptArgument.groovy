package org.fidata.gradle.packer.tasks.arguments

import groovy.transform.CompileStatic
import org.fidata.gradle.packer.template.OnlyExcept
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.Internal
import org.gradle.api.tasks.Optional

@CompileStatic
trait PackerOnlyExceptArgument extends PackerArgument {
  private OnlyExcept onlyExcept

  @Input
  @Optional
  OnlyExcept getOnlyExcept() {
    this.onlyExcept
  }

  void setOnlyExcept(OnlyExcept onlyExcept) {
    this.onlyExcept = onlyExcept
  }

  @Internal
  @Override
  List<Object> getCmdArgs() {
    List<Object> cmdArgs = (List<Object>)super.getCmdArgs()
    if (onlyExcept?.only?.size() > 0) {
      cmdArgs.add "-only=${ onlyExcept.only.join(',')}"
    } else if (onlyExcept?.except?.size() > 0) {
      cmdArgs.add "-except=${ onlyExcept.except.join(',')}"
    }
    cmdArgs
  }
}
