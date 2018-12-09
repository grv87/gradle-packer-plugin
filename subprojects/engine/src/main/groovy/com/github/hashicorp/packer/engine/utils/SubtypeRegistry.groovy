package com.github.hashicorp.packer.engine.utils

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
  private static final Class<T> T_CLASS = (Class<T>)new TypeToken<T>(this.class) { }.rawType
  private final Map<String, Class<? extends T>> typeRegistry = [:]
  private SimpleModule module = null

  @Synchronized
  void registerSubtype(String name, Class<? extends T> clazz) {
    if (typeRegistry.containsKey(name)) {
      throw new IllegalArgumentException(sprintf('%s with type %s is already registered', [T_CLASS.simpleName, name]))
    }
    module = null
    typeRegistry[name] = clazz
  }

  @Synchronized
  T newInstance(String name) {
    typeRegistry[name].getConstructor().newInstance()
  }

  @Override
  @Synchronized
  Module getModule(Mutability mutability) {
    if (this.@module == null) {
      this.@module = new SimpleModule()
      typeRegistry.each { Map.Entry<String, Class<? extends T>> entry ->
        this.@module.registerSubtypes(new NamedType(entry.value, entry.key))
      }
    }
    this.@module
  }

  protected static void registerRegistry(SubtypeRegistry<T> typeRegistry) {
    ObjectMapperFacade.registerCustomModuleProvider typeRegistry
  }
}
