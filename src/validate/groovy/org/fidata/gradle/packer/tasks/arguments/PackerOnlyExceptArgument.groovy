package org.fidata.gradle.packer.tasks.arguments

import groovy.transform.CompileStatic
import com.github.hashicorp.packer.template.OnlyExcept
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

  /*
   * WORKAROUND:
   * CodeNarc bug
   * Without getter we have error:
   * Call to super is not allowed in a trait
   * <grv87 2018-08-19>
   */
  @SuppressWarnings('UnnecessaryGetter')
  @Internal
  @Override
  List<Object> getCmdArgs() {
    List<Object> cmdArgs = (List<Object>)super.getCmdArgs()
    if (onlyExcept?.only?.empty == false) {
      cmdArgs.add "-only=${ onlyExcept.only.join(',')}"
    } else if (onlyExcept?.except?.empty  == false) {
      cmdArgs.add "-except=${ onlyExcept.except.join(',')}"
    }
    cmdArgs
  }
}
