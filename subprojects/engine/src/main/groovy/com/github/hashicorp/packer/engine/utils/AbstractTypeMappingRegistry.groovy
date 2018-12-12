package com.github.hashicorp.packer.engine.utils

import com.fasterxml.jackson.databind.Module
import com.fasterxml.jackson.databind.module.SimpleModule
import com.github.hashicorp.packer.engine.types.InterpolableObject
import groovy.transform.CompileStatic
import groovy.transform.PackageScope
import groovy.transform.Synchronized

@CompileStatic
class AbstractTypeMappingRegistry implements ModuleProvider {
  private final Map<Class<? extends InterpolableObject>, Map<Mutability, Class<? extends InterpolableObject>>> abstractTypeMappingRegistry = [:]
  private final Map<Mutability, SimpleModule> modules = [:]

  @PackageScope
  AbstractTypeMappingRegistry() {}

  @Synchronized
  <T extends InterpolableObject> void registerAbstractTypeMapping(Class<T> abstractClass, Class<? extends T> mutableClass, Class<? extends T> immutableClass) {
    if (abstractTypeMappingRegistry.containsKey(abstractClass)) {
      throw new IllegalArgumentException(sprintf('Abstract type mapping for type %s is already registered', [abstractClass.canonicalName]))
    }
    modules.clear()
    abstractTypeMappingRegistry[abstractClass] = [(Mutability.MUTABLE): mutableClass, (Mutability.IMMUTABLE): immutableClass]
  }

  @Synchronized
  <T extends InterpolableObject> T newInstance(Class<T> abstractClass, Mutability mutability) {
    (T)abstractTypeMappingRegistry[abstractClass][mutability].getConstructor().newInstance()
  }

  @Override
  @Synchronized
  Module getModule(Mutability mutability) {
    SimpleModule module = modules[mutability]
    if (module == null) {
      module = new SimpleModule()
      abstractTypeMappingRegistry.each { Map.Entry<Class<? extends InterpolableObject>, Map<Mutability, Class<? extends InterpolableObject>>> entry ->
        module.addAbstractTypeMapping entry.key, entry.value[mutability]
      }
      modules[mutability] = module
    }
    module
  }
}
