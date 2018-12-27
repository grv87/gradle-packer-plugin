package org.fidata.packer.engine.types

import org.fidata.packer.engine.types.base.InterpolableValue
import org.fidata.packer.engine.types.base.SimpleInterpolableString
import com.github.hashicorp.packer.template.Context
import groovy.transform.InheritConstructors
import groovy.transform.CompileStatic
import groovy.transform.KnownImmutable

import java.util.concurrent.Callable

// TOTHINK: Maybe we should accept other input types too ?
// (Not just string)
// (But why here only then?)
// Callable is required for InputFile etc. annotations
@CompileStatic
interface InterpolableFile<ThisInterface extends InterpolableFile<ThisInterface>> extends InterpolableValue<Object, File, ThisInterface>, Callable<File> {
  @KnownImmutable
  @InheritConstructors
  class ImmutableRaw<
    ThisInterface extends InterpolableFile<ThisInterface>,
    InterpolatedClass extends Interpolated<ThisInterface, AlreadyInterpolatedClass> & ThisInterface,
    AlreadyInterpolatedClass extends AlreadyInterpolated<ThisInterface> & ThisInterface
  > extends InterpolableValue.ImmutableRaw<SimpleInterpolableString, File, InterpolableFile, InterpolatedClass, AlreadyInterpolatedClass> implements InterpolableFile<ThisInterface> {
    protected static final File doInterpolatePrimitive(Context context, SimpleInterpolableString raw) {
      context.interpolatePath(raw.interpolate(context)).toFile()
    }

    @Override
    File call() {
      interpolated
    }
  }

  @InheritConstructors
  class Raw<
    ThisInterface extends InterpolableFile<ThisInterface>,
    InterpolatedClass extends Interpolated<ThisInterface, AlreadyInterpolatedClass> & ThisInterface,
    AlreadyInterpolatedClass extends AlreadyInterpolated<ThisInterface> & ThisInterface
  > extends InterpolableValue.Raw<SimpleInterpolableString, File, InterpolableFile, InterpolatedClass, AlreadyInterpolatedClass> implements InterpolableFile<ThisInterface> {
    protected static final File doInterpolatePrimitive(Context context, SimpleInterpolableString raw) {
      context.interpolatePath(raw.interpolate(context)).toFile()
    }

    @Override
    File call() {
      interpolated
    }
  }

  @KnownImmutable
  @InheritConstructors
  class Interpolated<
    ThisInterface extends InterpolableFile<ThisInterface>,
    AlreadyInterpolatedClass extends AlreadyInterpolated<ThisInterface> & ThisInterface
  > extends InterpolableValue.Interpolated<SimpleInterpolableString, File, InterpolableFile, AlreadyInterpolatedClass> implements InterpolableFile<ThisInterface> {
    @Override
    File call() {
      interpolated
    }
  }

  @InheritConstructors
  class AlreadyInterpolated<
    ThisInterface extends InterpolableFile<ThisInterface>
  > extends InterpolableValue.AlreadyInterpolated<SimpleInterpolableString, File, InterpolableFile> implements InterpolableFile<ThisInterface> {
    @Override
    File call() {
      interpolated
    }
  }
}
