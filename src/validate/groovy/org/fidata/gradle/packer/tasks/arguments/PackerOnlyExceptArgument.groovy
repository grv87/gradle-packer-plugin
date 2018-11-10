package org.fidata.gradle.packer.tasks.arguments

import groovy.transform.CompileStatic
import com.github.hashicorp.packer.template.OnlyExcept
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.Internal
import org.gradle.api.tasks.Optional

@CompileStatic
trait PackerOnlyExceptArgument extends PackerOnlyExceptReadOnlyArgument {
  void setOnlyExcept(OnlyExcept onlyExcept) {
    this.onlyExcept = onlyExcept
  }
}
