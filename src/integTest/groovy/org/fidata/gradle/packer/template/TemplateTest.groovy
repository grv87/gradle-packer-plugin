/*
 * Integration test for parsing Interpolable and nested objects
 * Copyright © 2018  Basil Peace
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
package org.fidata.gradle.packer.template

import com.google.common.reflect.ClassPath
import junitparams.JUnitParamsRunner
import org.junit.runner.RunWith
import com.fasterxml.jackson.databind.ObjectMapper
import groovy.transform.CompileStatic
import junitparams.Parameters
import junitparams.naming.TestCaseName
import org.junit.Test
import org.apache.commons.io.FilenameUtils

@RunWith(JUnitParamsRunner)
@CompileStatic
class TemplateTest {
  @Test
  @Parameters
  @TestCaseName('{1}')
  void testParser(final File templateFile, final String testName) {
    ObjectMapper mapper = new ObjectMapper()
    Template template = mapper.readValue(templateFile, Template)
    assert Template.isInstance(template)
  }

  static Object[] parametersForTestParser() {
    ClassPath.from(TemplateTest.class.classLoader).getResources().findAll { it.resourceName.startsWith('org/fidata/gradle/packer') && it.resourceName.endsWith('.json') }.collect { [new File(it.url().toURI()), FilenameUtils.getBaseName(it.url().path)].toArray() }.toArray(new Object[0])
  }
}
