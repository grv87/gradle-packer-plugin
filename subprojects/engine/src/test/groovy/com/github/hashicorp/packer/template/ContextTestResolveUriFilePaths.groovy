package com.github.hashicorp.packer.template

import static org.apache.commons.lang3.SystemUtils.IS_OS_WINDOWS
import org.junit.BeforeClass
import java.nio.file.Paths
import groovy.transform.CompileStatic
import org.junit.Test
import org.junit.runner.RunWith
import junitparams.JUnitParamsRunner
import junitparams.Parameters
import junitparams.naming.TestCaseName
import java.nio.file.Path

/**
 * Port of TestDownloadableURL_FilePaths
 */
@RunWith(JUnitParamsRunner)
@CompileStatic
class ContextTestResolveUriFilePaths {
  public static final File tf = File.createTempFile('', 'packer')
  public static final Path tfPath = tf.toPath().toRealPath().normalize()

  @BeforeClass
  static void setup() {
    tf.deleteOnExit()
  }

  private static final String FILE_PREFIX = 'file://'

  // If we're running windows, then absolute URIs are `/`-prefixed.
  private static final String PLATFORM_PREFIX = IS_OS_WINDOWS ? '/' : ''

  private static final Object[] parametersForTest() {
    // Relative filepath
    List<List> result = [
      [
        tf.parentFile,
        tfPath.fileName,
        "$FILE_PREFIX$PLATFORM_PREFIX${ tfPath.toString().replace('\\' as char, '/' as char) }",
      ],
    ]
    // Test some cases with and without a schema prefix
    ['', FILE_PREFIX + PLATFORM_PREFIX].each { String prefix ->
      // Nonexistent file
      result.add([
        Paths.get(''),
        prefix + 'i/dont/exist',
        null
      ])
      // Good file (absolute)
      result.add([
        Paths.get(''),
        prefix + tfPath,
        "$FILE_PREFIX$PLATFORM_PREFIX${ tfPath.toString().replace('\\' as char, '/' as char) }",
      ])
    }
    result.collect { (it + [it[2] ? "== \"${ it[2] }\"".toString() : 'doesn\'t throw exception']).toArray() }.toArray()
  }

  @Test
  @Parameters
  @TestCaseName('resolveUri("{0}") {3}')
  void test(final Path cwd, final String original, final String expected) {
    String result = new Context(null, null, null, cwd, null).resolveUri(original).toString()
    if (expected) {
      assert result == expected
    }
  }
}
