package org.fidata.gradle.packer.tasks.arguments

import groovy.transform.CompileStatic
import com.github.hashicorp.packer.template.OnlyExcept
import org.fidata.packer.engine.annotations.ExtraProcessed

@CompileStatic
trait PackerOnlyExceptReadOnlyArgument extends PackerArgument {
  protected /* TOTEST */ OnlyExcept onlyExcept

  @ExtraProcessed
  OnlyExcept getOnlyExcept() {
    this.onlyExcept
  }

  /*
   * WORKAROUND:
   * CodeNarc bug
   * Without getter we have error:
   * Call to super is not allowed in a trait
   * <grv87 2018-08-19>
   */
  @SuppressWarnings('UnnecessaryGetter')
  // TOTEST: @Internal
  @Override
  List<String> getCmdArgs() {
    List<String> cmdArgs = super.getCmdArgs()
    if (onlyExcept?.only?.empty == Boolean.FALSE) {
      cmdArgs.add "-only=${ onlyExcept.only.join(',')}"
    } else if (onlyExcept?.except?.empty == Boolean.FALSE) {
      cmdArgs.add "-except=${ onlyExcept.except.join(',')}"
    }
    cmdArgs
  }
}
