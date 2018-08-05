package org.fidata.gradle.packer.template

import org.fidata.gradle.packer.template.internal.TemplateObject
import org.fidata.gradle.packer.template.types.TemplateString
import org.gradle.api.tasks.Console
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.Internal

class BuilderHeader extends TemplateObject {
  @Internal
  TemplateString name

  @Input // TODO
  String type

  @Input // TODO
  String getInterpolatedName() {
    name.interpolatedValue
  }

  @Override
  protected void doInterpolate(Context ctx) {
    name.interpolate(ctx)
  }
}
