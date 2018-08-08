package org.fidata.gradle.packer.template

import com.google.common.reflect.ClassPath
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
  void testParser(final File templateFile) {
    ObjectMapper mapper = new ObjectMapper()
    // Template template = mapper.readValue(Resources.getResource(this.class, resourceName), Template)
    Template template = mapper.readValue(templateFile, Template)
    assert Template.isInstance(template)
  }
  static File[] parametersForTestParser() {
    // new File(Resources.getResource(TemplateTest /* this.class */, '').getPath()).listFiles()
    /*[
      'builder-null.json'
    ].collect {
      new File(Resources.getResource(TemplateTest, it).getPath())
    }*/
    ClassPath.from(TemplateTest.class.classLoader).getResources().findAll { it.resourceName.startsWith('org/fidata/gradle/packer') && it.resourceName.endsWith('.json') }.collect { new File(it.url().path) }
    // new File(TemplateTest.class.getResource('').getPath()).listFiles()
    /*Resources.getResource(this.class)
    Resources
    [
      ['spikes/reference.json'],
      ['provisioner-file/file.json'],
    ]*/
  }

}
