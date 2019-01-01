package org.fidata.packer.engine.types

import com.github.hashicorp.packer.template.Context
import groovy.transform.CompileStatic
import groovy.transform.InheritConstructors
import groovy.transform.KnownImmutable
import org.fidata.packer.engine.types.base.InterpolableValue
import org.fidata.packer.engine.types.base.SimpleInterpolableString

@CompileStatic
interface InterpolableFileContent extends InterpolableValue<SimpleInterpolableString, SimpleInterpolableString, InterpolableFileContent> {
  @KnownImmutable
  @InheritConstructors
  final class ImmutableRaw extends InterpolableValue.ImmutableRaw<SimpleInterpolableString, SimpleInterpolableString, InterpolableFileContent, Interpolated, AlreadyInterpolated> implements InterpolableFileContent {
    protected static final SimpleInterpolableString doInterpolatePrimitive(Context context, SimpleInterpolableString raw) {
      new SimpleInterpolableString(context.interpolatePath(raw.interpolate(context)).toFile().text)
    }
  }

  @InheritConstructors
  final class Raw extends InterpolableValue.Raw<SimpleInterpolableString, SimpleInterpolableString, InterpolableFileContent, Interpolated, AlreadyInterpolated> implements InterpolableFileContent {
    protected static final SimpleInterpolableString doInterpolatePrimitive(Context context, SimpleInterpolableString raw) {
      new SimpleInterpolableString(context.interpolatePath(raw.interpolate(context)).toFile().text)
    }
  }

  @InheritConstructors
  final class Interpolated extends InterpolableValue.Interpolated<SimpleInterpolableString, SimpleInterpolableString, InterpolableFileContent, AlreadyInterpolated> implements InterpolableFileContent { }

  @KnownImmutable
  @InheritConstructors
  final class AlreadyInterpolated extends InterpolableValue.AlreadyInterpolated<SimpleInterpolableString, SimpleInterpolableString, InterpolableFileContent> implements InterpolableFileContent { }
}
