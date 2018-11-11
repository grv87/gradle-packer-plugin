package com.github.hashicorp.packer.engine.types

import com.github.hashicorp.packer.template.Context
import com.google.common.reflect.TypeToken
import groovy.transform.CompileDynamic
import groovy.transform.CompileStatic
import groovy.transform.EqualsAndHashCode
import groovy.transform.AutoClone
import groovy.transform.AutoCloneStyle
import com.fasterxml.jackson.annotation.JsonValue
import com.github.hashicorp.packer.engine.exceptions.InvalidRawValueClass
import groovy.transform.Synchronized

import java.util.concurrent.Callable
import java.util.concurrent.Semaphore
import java.util.function.Supplier

@AutoClone(style = AutoCloneStyle.SIMPLE)
// equals is required for Gradle up-to-date checking
@EqualsAndHashCode(includes = ['interpolatedValue'])
// @AutoExternalize(excludes = ['rawValue']) // TODO: Groovy 2.5.0
@CompileStatic
// Serializable and Externalizable are required for Gradle up-to-date checking
abstract class InterpolableValue<Source, Target extends Serializable> extends InterpolableObject implements Externalizable, Supplier<Target> {
  // @SuppressWarnings("UnstableApiUsage")
  // static final Class<Target> TARGET_CLASS = (Class<Target>)new TypeToken<Target>(this.class) { }.rawType

  @JsonValue
  Source rawValue = null

  // This constructor is required for Externalizable and AutoClone
  protected InterpolableValue() {
  }

  protected InterpolableValue(Source rawValue) {
    this.@rawValue = rawValue
  }

  private InterpolableValue(Supplier<Target> defaultSupplier ) {
    this.@rawValue = rawValue
  }

  @Override
  protected final void doInterpolate() { }

  private final Callable<Boolean> nullIf = null

  private final Supplier<Target> defaultSupplier = null

  private Supplier<Target> interpolatedValue = new Supplier<Target>() {
    @Override
    Target get() {
      if (rawValue != null) {
        // result.interpolatedValue =     if (!isDefault || rawValue != null /* Means that somebody reassigned value to custom */) {
        interpolatedValue = doInterpolatePrimitive(rawValue)
        isDefault = false
      }
    }
  }

  private final Semaphore interpolation = new Semaphore(1)
  private volatile long lockedBy

  /*
   * CAVEAT:
   * We use dynamic compiling to run
   * overloaded version of doInterpolatePrimitive
   * depending on rawValue actual type
   */
  @CompileDynamic

  /**
   * @serial Interpolated value
   */
  @Override
  final Target get() throws IllegalStateException {
    if (interpolation.tryAcquire()) {

    }
    if (nullIf?.call()) {
      null
    }
    if (rawValue != null) {
      Target result = doInterpolatePrimitive(rawValue)
      if (result != null) {
        return result
      }
    }
    defaultSupplier?.get()
  }

  final Target get(Context context) {
    interpolate(context)
    this.interpolatedValue.get()
  }

  protected static /* TOTEST */ Target doInterpolatePrimitive(Object rawValue) {
    throw new InvalidRawValueClass(rawValue)
  }

  /* TODO protected Target doInterpolatePrimitive(Target rawValue) {
    this.@rawValue = rawValue
  }*/

  @SuppressWarnings('unused') // IDEA bug
  private static final long serialVersionUID = 7881876550613522317L

  /**
   * @serialData interpolatedValue
   */
  @Override
  void writeExternal(ObjectOutput out) throws IOException {
    out.writeObject(get())
  }

  @Override
  void readExternal(ObjectInput oin) throws IOException, ClassNotFoundException {
    interpolatedValue = (Target)oin.readObject()
    interpolated = true // TODO: use isDefault/isInterpolatedWithoutContext ?
  }

  static class Builder<V extends InterpolableValue<Source, Target>> {
    private V result

    Builder(Class<V> clazz) {
      V result = (V) clazz.newInstance() /* TODO */
    }

    Builder<V> withDefault(Target interpolatedValue) {
      result.@interpolatedValue = interpolatedValue
      this
    }

    Builder<V> withDefault(Callable<Target> provider) {
      result.@interpolatedValue = interpolatedValue
      this
    }

    Builder<V> withDefault(Supplier<Target> provider) {
      result.@interpolatedValue = interpolatedValue
      this
    }

    Builder<V> nullIf(Callable<Boolean> provider) {
      result.@nullIf = provider
      this
    }


    V build() {
      if (result.interpolatedValue == null)
        result.interpolatedValue =     if (!isDefault || rawValue != null /* Means that somebody reassigned value to custom */) {
        interpolatedValue = doInterpolatePrimitive(rawValue)
        isDefault = false
      }
      result
    }

  }

  // This is used to create instances with default values
  protected static final <V extends InterpolableValue<Source, Target>> V withDefault(Class<V> clazz, Target interpolatedValue) {
    V result =  (V)clazz.newInstance() /* TODO */
    result.@interpolatedValue = interpolatedValue
    result.@isDefault = true
    result
  }

  protected static final <V extends InterpolableValue<Source, Target>> V withDefault(Class<V> clazz, Callable<Target> provider) {
    V result =  (V)clazz.newInstance() /* TODO */
    result.@interpolatedValue = interpolatedValue // TODO
    result.@isDefault = true
    result
  }

  protected static final <V extends InterpolableValue<Source, Target>> V withDefault(Class<V> clazz, Supplier<Target> provider) {
    V result =  (V)clazz.newInstance() /* TODO */
    result.@interpolatedValue = interpolatedValue // TODO
    result.@isDefault = true
    result
  }
}
