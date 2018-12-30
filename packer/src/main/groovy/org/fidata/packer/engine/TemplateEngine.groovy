package org.fidata.packer.engine

import com.github.hashicorp.packer.template.Builder
import com.github.hashicorp.packer.template.PostProcessor
import com.github.hashicorp.packer.template.Provisioner
import com.github.hashicorp.packer.template.Template
import com.github.hashicorp.packer.provisioner.File

final class TemplateEngine extends AbstractEngine<Template> {
  TemplateEngine(Mutability mutability) {
    super(mutability)
  }
  void register() {
    Builder.register this

    Provisioner.register this
    File.register this

    PostProcessor.register this
  }
}
