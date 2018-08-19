package org.fidata.gradle.packer.tasks.arguments

import groovy.transform.CompileStatic
import org.gradle.api.tasks.Internal
import org.gradle.api.tasks.Optional

@CompileStatic
trait PackerVarArgument extends PackerArgument {
  private Map<String, Object> variables

  @Internal
  @Optional
  Map<String, Object> getVariables() {
    this.variables
  }

  void setVariables(Map<String, Object> variables) {
    this.variables = variables
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
    List<Object> newCmdArgs = (List<Object>)variables?.collectMany { String key, Object value ->
      [
        '-var',
        "${ key }=${ value }"
      ]
    }
    if (newCmdArgs) {
      cmdArgs.addAll cmdArgs
    }
    cmdArgs
  }
}
