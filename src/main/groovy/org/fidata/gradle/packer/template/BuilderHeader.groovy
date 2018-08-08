package org.fidata.gradle.packer.template

import org.fidata.gradle.packer.template.internal.InterpolableObject
import org.fidata.gradle.packer.template.types.InterpolableString
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.Internal

class BuilderHeader extends InterpolableObject {
  @Internal
  InterpolableString name

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
