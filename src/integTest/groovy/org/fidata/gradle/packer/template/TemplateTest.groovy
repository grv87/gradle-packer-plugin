package org.fidata.gradle.packer.template

import junitparams.JUnitParamsRunner
import org.junit.runner.RunWith
import com.fasterxml.jackson.databind.ObjectMapper
import com.google.common.io.Resources
import groovy.transform.CompileStatic
import junitparams.Parameters
import junitparams.naming.TestCaseName
import org.fidata.gradle.packer.template.Template
import org.junit.Test

@RunWith(JUnitParamsRunner)
//@CompileStatic
class TemplateTest {
  @Test
  @Parameters
  @TestCaseName('{index}: {0}')
  void testParser(final String resourceName) {
    ObjectMapper mapper = new ObjectMapper()
    Template template = mapper.readValue(Resources.getResource(this.class, resourceName), Template)
    assert Template.isInstance(template)
  }
  static Object[][] parametersForTestParser() {
    [
      ['spikes/reference.json'],
      ['provisioner-file/file.json'],
    ]
  }

}
