package org.fidata.gradle.packer.template.common

import com.fasterxml.jackson.annotation.JsonProperty

class ShellLocal {
  List<String> inline

  @JsonProperty('inline_shebang')
  String inlineShebang

  @JsonProperty('tempfile_extension')
  String tempfileExtension

  String script

  List<String> scripts

  @JsonProperty('environment_vars')
  List<String> environmentVars

  @JsonProperty('env_var_format')
  String envVarFormat

  @JsonProperty('execute_command')
  List<String> executeCommand

  @JsonProperty('use_linux_pathing')
  List<String> useLinuxPathing
}
