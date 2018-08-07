package org.fidata.gradle.packer.template

import com.fasterxml.jackson.databind.ObjectMapper
import org.junit.Test

class TemplateTest {
  @Test
  void test() {
    ObjectMapper mapper = new ObjectMapper()
    Builder builder = mapper.readValue(, Builder)
    assert VirtualBoxOvf.isInstance(builder)
  }
}
