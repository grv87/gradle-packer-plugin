/*
 * Integration test for parsing Packer templates with Jackson
 * Copyright ©  Basil Peace
 *
 * This file is part of gradle-packer-plugin.
 *
 * This plugin is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License
 * as published by the Free Software Foundation, either version 3
 * of the License, or (at your option) any later version.
 *
 * This plugin is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this plugin.  If not, see <https://www.gnu.org/licenses/>.
 */
package com.github.hashicorp.packer.template

import static org.apache.commons.io.FilenameUtils.getBaseName
import com.google.common.reflect.ClassPath
import junitparams.JUnitParamsRunner
import org.fidata.gradle.packer.PackerBasePlugin
import org.junit.runner.RunWith
import groovy.transform.CompileStatic
import junitparams.Parameters
import junitparams.naming.TestCaseName
import org.junit.Test

/**
 * Tests for parsing Packer templates with Jackson
 */
@RunWith(JUnitParamsRunner)
@CompileStatic
class TemplateTest {
  @Test
  @Parameters
  @TestCaseName('{1}')
  void testParser(final File templateFile, final String ignored) {
    Template template = Template.readFromFile(templateFile)
    assert Template.isInstance(template)

    new StringWriter().withWriter { Writer writer ->
      template.writeValue(writer)

      String string = writer.toString()

      assert string.length() > 0

      Template template2 = Template.readValue(string)
      assert Template.isInstance(template2)
    }
  }

  private static Object[] parametersForTestParser() {
    Object[] result = ClassPath.from(Thread.currentThread().contextClassLoader ?: this.classLoader).resources.findAll { ClassPath.ResourceInfo it ->
      it.resourceName.startsWith('org/fidata/gradle/packer') && it.resourceName.endsWith('.json')
    }.collect { ClassPath.ResourceInfo it ->
      [new File(it.url().toURI()), getBaseName(it.url().path)].toArray(new Object[2])
    }.toArray(new Object[0])
    assert result.length > 0
    result
  }
}
