package org.fidata.packer.engine

import com.fasterxml.jackson.core.Version
import com.fasterxml.jackson.databind.Module
import com.fasterxml.jackson.databind.jsontype.NamedType
import org.fidata.packer.engine.types.base.InterpolableObject
import com.google.common.reflect.TypeToken
import groovy.transform.CompileStatic
import groovy.transform.Synchronized

@CompileStatic
final class SubtypeRegistry<T extends InterpolableObject> extends Module {
  @SuppressWarnings('UnstableApiUsage')
  private final String className = new TypeToken<T>(this.class) { }.rawType.simpleName
  private final Map<String, Class<? extends T>> subtypeRegistry = [:]

  @Synchronized
  void registerSubtype(String name, Class<? extends T> clazz) {
    if (subtypeRegistry.containsKey(name)) {
      throw new IllegalArgumentException(sprintf('%s with type %s is already registered', [className, name]))
    }
    subtypeRegistry[name] = clazz
  }

  @Synchronized
  Class<? extends T> getAt(String name) {
    subtypeRegistry[name]
  }

  @Override
  String getModuleName() {
    return null
  }

  @Override
  Version version() {
    return null
  }

  @Override
  void setupModule(SetupContext context) {
    subtypeRegistry.each { String key, Class<? extends T> value ->
      context.registerSubtypes(new NamedType(value, key))
    }
  }
}
