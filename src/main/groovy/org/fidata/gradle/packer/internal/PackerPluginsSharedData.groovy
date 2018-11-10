package org.fidata.gradle.packer.internal

import com.github.hashicorp.packer.template.Template
import groovy.transform.CompileStatic

@CompileStatic
class PackerPluginsSharedData {
  Map<String, SourceSetDescriptor> sourceSets = []

  static final class SourceSetDescriptor {
    List<TemplateDescriptor> templateDescriptors = []
    List<BuildDescriptor> buildDescriptors = []

    static final class TemplateDescriptor {
      final String projectPath
      final Template template

      TemplateDescriptor(String projectPath, Template template) {
        this.projectPath = projectPath
        this.template = template
      }
    }

    static final class BuildDescriptor {
      final String projectPath
      final Template template
      final String buildName

      BuildDescriptor(String projectPath, Template template, String buildName) {
        this.projectPath = projectPath
        this.template = template
        this.buildName = buildName
      }
    }
  }
}
