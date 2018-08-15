package org.fidata.gradle.packer.template.internal

import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.core.JsonProcessingException
import com.fasterxml.jackson.databind.DeserializationContext
import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.deser.std.StdDeserializer
import com.fasterxml.jackson.databind.node.ObjectNode
import com.sun.javaws.exceptions.InvalidArgumentException
import groovy.transform.CompileStatic

@CompileStatic
class TypedDynamicDeserializer<Class> extends StdDeserializer<Class> {
  private Map<String, Class> registry = new HashMap<String, Class>()

  TypedDynamicDeserializer() {
    super(Class.class)
  }

  void register(String type, Class aClass) {
    if (registry.containsKey(type)) {
      throw new InvalidArgumentException(sprintf('%s with type %s is already registered', [Class.simpleName, type])
    }
    registry.put(type, aClass)
  }

  @Override
  Class deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException, JsonProcessingException {
    ObjectMapper mapper = (ObjectMapper) jp.getCodec()
    ObjectNode root = (ObjectNode) mapper.readTree(jp)
    Class aClass = null
    Iterator<Map.Entry<String, JsonNode>> elementsIterator =
      root.getFields()
    while (elementsIterator.hasNext())
    {
      Map.Entry<String, JsonNode> element=elementsIterator.next()
      String name = element.getKey()
      if (registry.containsKey(name)) {
        aClass = registry.get(name)
        break
      }
    }
    if (aClass == null) { return null }
    return mapper.readValue(root, aClass)
  }
}
