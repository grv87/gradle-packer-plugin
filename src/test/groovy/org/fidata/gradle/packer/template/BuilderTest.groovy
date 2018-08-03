package org.fidata.gradle.packer.template

import com.fasterxml.jackson.databind.ObjectMapper
import org.fidata.gradle.packer.template.builder.VirtualBoxOvf
import org.junit.Test

class BuilderTest {
  @Test
  void test() {
    ObjectMapper mapper = new ObjectMapper()
    Builder builder = mapper.readValue('{ "type": "virtualbox-ovf" }', Builder)
    assert VirtualBoxOvf.isInstance(builder)
  }
}
