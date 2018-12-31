package org.fidata.gradle.packer.tasks.arguments

import org.fidata.packer.engine.annotations.ExtraProcessed
import org.gradle.api.provider.MapProperty
import groovy.transform.CompileStatic

@CompileStatic
trait PackerVarArgument extends PackerArgument {
  @ExtraProcessed
  final MapProperty<String, String> variables = project.objects.mapProperty(String, String).empty()

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
    variables.get().each { String key, String value ->
      cmdArgs.add '-var'
      cmdArgs.add "$key=$value"
    }
    cmdArgs
  }
}
