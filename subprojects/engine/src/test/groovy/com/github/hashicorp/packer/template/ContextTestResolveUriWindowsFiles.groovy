package com.github.hashicorp.packer.template

import org.junit.Ignore

import static org.apache.commons.io.FilenameUtils.separatorsToUnix
import groovy.transform.CompileStatic
import org.junit.BeforeClass
import org.junit.ClassRule
import org.junit.Test
import org.junit.rules.TemporaryFolder
import org.junit.runner.RunWith
import junitparams.JUnitParamsRunner
import junitparams.Parameters
import junitparams.naming.TestCaseName

/**
 * Port of TestDownloadableURL_WindowsFiles
 */
@RunWith(JUnitParamsRunner)
@CompileStatic
@Ignore
class ContextTestResolveUriWindowsFiles {
  @ClassRule
  public static final TemporaryFolder temporaryFolder = new TemporaryFolder()
  public static File testFixturesDir

  @BeforeClass
  static void setupTestFixtures() {
    testFixturesDir = temporaryFolder.newFolder('test-fixtures')
    File someDir = new File(testFixturesDir, 'SomeDir')
    assert someDir.mkdir()
    new File(someDir, 'myfile.txt').createNewFile()
    NATIVE_PATH_TO_TEST_FIXTURES = testFixturesDir.absoluteFile.toString()
  }

  private static final String PORTABLE_PATH_TO_TEST_FIXTURES = separatorsToUnix(NATIVE_PATH_TO_TEST_FIXTURES)
  
  private static String NATIVE_PATH_TO_TEST_FIXTURES // TODO

  private static final Object[] parametersForTest() {
    [
      // TODO: add different directories
      [
        "$NATIVE_PATH_TO_TEST_FIXTURES\\SomeDir\\myfile.txt",
        "file:///$PORTABLE_PATH_TO_TEST_FIXTURES/SomeDir/myfile.txt",
      ],
      [ // without the drive makes this native path a relative file:// uri
        "test-fixtures\\SomeDir\\myfile.txt",
        "file:///$PORTABLE_PATH_TO_TEST_FIXTURES/SomeDir/myfile.txt",
      ],
      [ // without the drive makes this native path a relative file:// uri
        "test-fixtures/SomeDir/myfile.txt",
        "file:///$PORTABLE_PATH_TO_TEST_FIXTURES/SomeDir/myfile.txt",
      ],
      [ // UNC paths being promoted to smb:// uri scheme.
        "\\\\localhost\\C\$\\$NATIVE_PATH_TO_TEST_FIXTURES\\SomeDir\\myfile.txt",
        "smb://localhost/C\$/$PORTABLE_PATH_TO_TEST_FIXTURES/SomeDir/myfile.txt",
      ],
      [ // Absolute uri (incorrect slash type)
        "file:///$NATIVE_PATH_TO_TEST_FIXTURES\\SomeDir\\myfile.txt",
        "file:///$PORTABLE_PATH_TO_TEST_FIXTURES/SomeDir/myfile.txt",
      ],
      [ // Absolute uri (existing and mis-spelled)
        "file:///$NATIVE_PATH_TO_TEST_FIXTURES/Somedir/myfile.txt",
        "file:///$PORTABLE_PATH_TO_TEST_FIXTURES/SomeDir/myfile.txt",
      ],
      [ // Absolute path (non-existing)
        "\\absolute\\path\\to\\non-existing\\file.txt",
        "file:///absolute/path/to/non-existing/file.txt",
      ],
      [ // Absolute paths (existing)
        "$NATIVE_PATH_TO_TEST_FIXTURES/SomeDir/myfile.txt",
        "file:///$PORTABLE_PATH_TO_TEST_FIXTURES/SomeDir/myfile.txt",
      ],
      [ // Relative path (non-existing)
        "./nonexisting/relative/path/to/file.txt",
        "file://./nonexisting/relative/path/to/file.txt",
      ],
      [ // Relative path (existing)
        "./test-fixtures/SomeDir/myfile.txt",
        "file:///$PORTABLE_PATH_TO_TEST_FIXTURES/SomeDir/myfile.txt",
      ],
      [ // Absolute uri (existing and with `/` prefix)
        "file:///$PORTABLE_PATH_TO_TEST_FIXTURES/SomeDir/myfile.txt",
        "file:///$PORTABLE_PATH_TO_TEST_FIXTURES/SomeDir/myfile.txt",
      ],
      [ // Absolute uri (non-existing and with `/` prefix)
        "file:///path/to/non-existing/file.txt",
        "file:///path/to/non-existing/file.txt",
      ],
      [ // Absolute uri (non-existing and missing `/` prefix)
        "file://path/to/non-existing/file.txt",
        "file://path/to/non-existing/file.txt",
      ],
      [ // Absolute uri and volume (non-existing and with `/` prefix)
        "file:///T:/path/to/non-existing/file.txt",
        "file:///T:/path/to/non-existing/file.txt",
      ],
      [ // Absolute uri and volume (non-existing and missing `/` prefix)
        "file://T:/path/to/non-existing/file.txt",
        "file://T:/path/to/non-existing/file.txt",
      ]
    ]*.toArray().toArray()
  }

  @Test
  @Parameters
  @TestCaseName('resolveUri("{0}") == "{1}"')
  void test(final String original, final String expected) {
    assert new Context(null, null, null, temporaryFolder.root.toPath()).resolveUri(original).toString() == expected
  }
}
