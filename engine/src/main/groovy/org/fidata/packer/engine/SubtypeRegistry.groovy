package org.fidata.packer.engine

import com.fasterxml.jackson.core.Version
import com.fasterxml.jackson.databind.Module
import com.fasterxml.jackson.databind.jsontype.NamedType
import org.fidata.packer.engine.types.base.InterpolableObject
import com.google.common.reflect.TypeToken
import groovy.transform.CompileStatic
import org.fidata.version.VersionAdapter
// import groovy.transform.Synchronized

@CompileStatic
abstract class SubtypeRegistry<BaseType extends InterpolableObject<BaseType>> extends Module {
  @SuppressWarnings('UnstableApiUsage')
  private final String moduleName = new TypeToken<SubtypeRegistry<BaseType>>(this.class) { }.toString()
  private final String tClassName = new TypeToken<BaseType>(this.class) { }.toString()
  private final Map<String, Class<? extends BaseType>> subtypeRegistry = [:]

  private SubtypeRegistry() {}

  static <T extends InterpolableObject> SubtypeRegistry<T> forType(Class<T> baseType) {
    new SubtypeRegistry<T>(){ } // TOTEST
  }

  // @Synchronized
  void registerSubtype(String name, Class<? extends BaseType> clazz) {
    if (subtypeRegistry.containsKey(name)) {
      throw new IllegalArgumentException(sprintf('%s with type %s is already registered', [tClassName, name]))
    }
    subtypeRegistry[name] = clazz
  }

  // @Synchronized
  Class<? extends BaseType> getAt(String name) {
    subtypeRegistry[name]
  }

  @Override
  String getModuleName() {
    return moduleName
  }

  @Lazy
  private VersionAdapter version = VersionAdapter.mavenVersionFor(this.class.classLoader, 'org.fidata', 'gradle-packer-plugin') // TODO: ???

  @Override
  Version version() {
    return this.version.asJackson()
  }

  @Override
  void setupModule(SetupContext context) {
    subtypeRegistry.each { String key, Class<? extends BaseType> value ->
      context.registerSubtypes(new NamedType(value, key))
    }
  }
}
