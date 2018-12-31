package org.fidata.gradle.packer.tasks.arguments

import groovy.transform.CompileStatic
import com.github.hashicorp.packer.template.OnlyExcept

@CompileStatic
trait PackerOnlyExceptArgument extends PackerOnlyExceptReadOnlyArgument {
  void setOnlyExcept(OnlyExcept onlyExcept) {
    this.onlyExcept = onlyExcept
  }
}
