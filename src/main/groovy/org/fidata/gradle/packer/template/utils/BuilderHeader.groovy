package org.fidata.gradle.packer.template.utils

import org.fidata.gradle.packer.template.Context
import org.fidata.gradle.packer.template.internal.InterpolableObject
import org.fidata.gradle.packer.template.types.InterpolableString
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.Internal

class BuilderHeader extends InterpolableObject {
  @Input
  InterpolableString name

  @Input
  String type

  @Override
  protected void doInterpolate(Context ctx) {
    name.interpolate ctx
  }
}
