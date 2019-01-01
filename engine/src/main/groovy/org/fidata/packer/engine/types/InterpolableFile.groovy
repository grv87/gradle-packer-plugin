package org.fidata.packer.engine.types

import groovy.transform.SelfType
import org.fidata.packer.engine.types.base.InterpolableValue
import org.fidata.packer.engine.types.base.SimpleInterpolableString
import com.github.hashicorp.packer.template.Context
import groovy.transform.InheritConstructors
import groovy.transform.CompileStatic
import groovy.transform.KnownImmutable
import java.util.concurrent.Callable

// Callable is required for InputFile etc. annotations
@CompileStatic
interface InterpolableFile extends InterpolableValue<SimpleInterpolableString, File, InterpolableFile>, Callable<File> {
  @SelfType(InterpolableFile)
  private trait InterpolableFileImpl {
    @Override
    File call() {
      interpolated
    }
  }

  @KnownImmutable
  @InheritConstructors
  final class ImmutableRaw extends InterpolableValue.ImmutableRaw<SimpleInterpolableString, File, InterpolableFile, Interpolated, AlreadyInterpolated> implements InterpolableFile, InterpolableFileImpl {
    protected static final File doInterpolatePrimitive(Context context, SimpleInterpolableString raw) {
      context.interpolatePath(raw.interpolate(context)).toFile()
    }
  }

  @InheritConstructors
  final class Raw extends InterpolableValue.Raw<SimpleInterpolableString, File, InterpolableFile, Interpolated, AlreadyInterpolated> implements InterpolableFile, InterpolableFileImpl {
    protected static final File doInterpolatePrimitive(Context context, SimpleInterpolableString raw) {
      context.interpolatePath(raw.interpolate(context)).toFile()
    }
  }

  @KnownImmutable
  @InheritConstructors
  final class Interpolated extends InterpolableValue.Interpolated<SimpleInterpolableString, File, InterpolableFile, AlreadyInterpolated> implements InterpolableFile, InterpolableFileImpl { }

  @InheritConstructors
  final class AlreadyInterpolated extends InterpolableValue.AlreadyInterpolated<SimpleInterpolableString, File, InterpolableFile> implements InterpolableFile, InterpolableFileImpl { }
}
