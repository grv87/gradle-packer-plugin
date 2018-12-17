package com.github.hashicorp.packer.engine

import com.fasterxml.jackson.databind.Module
import com.fasterxml.jackson.databind.jsontype.NamedType
import com.fasterxml.jackson.databind.module.SimpleModule
import com.github.hashicorp.packer.engine.types.InterpolableObject
import com.google.common.reflect.TypeToken
import groovy.transform.CompileStatic
import groovy.transform.Synchronized

@CompileStatic
class SubtypeRegistry<T extends InterpolableObject> implements ModuleProvider {
  @SuppressWarnings('UnstableApiUsage')
  private final Class<T> tClass = (Class<T>)new TypeToken<T>(this.class) { }.rawType
  private final Map<String, Class<? extends T>> subtypeRegistry = [:]
  private SimpleModule module = null

  @Synchronized
  void registerSubtype(String name, Class<? extends T> clazz) {
    if (subtypeRegistry.containsKey(name)) {
      throw new IllegalArgumentException(sprintf('%s with type %s is already registered', [tClass.simpleName, name]))
    }
    module = null
    subtypeRegistry[name] = clazz
  }

  @Synchronized
  Class<? extends T> getAt(String name) {
    subtypeRegistry[name]
  }

  @Override
  @Synchronized
  Module getModule(Mutability mutability) {
    if (this.@module == null) {
      this.@module = new SimpleModule()
      subtypeRegistry.each { Map.Entry<String, Class<? extends T>> entry ->
        this.@module.registerSubtypes(new NamedType(entry.value, entry.key))
      }
    }
    this.@module
  }

  void registerRegistry(Engine engine) {
    engine.registerCustomModuleProvider this
  }
}
