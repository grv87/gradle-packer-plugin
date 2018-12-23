package com.github.hashicorp.packer.template

import org.junit.AfterClass

import static org.apache.commons.io.FilenameUtils.separatorsToUnix
import groovy.transform.CompileStatic
import org.junit.Test
import org.junit.runner.RunWith
import junitparams.JUnitParamsRunner
import junitparams.Parameters
import junitparams.naming.TestCaseName

/**
 * Port of TestDownloadableURL_WindowsFiles
 */
@RunWith(JUnitParamsRunner)
@CompileStatic
class ContextTestResolveUriWindowsFiles {
  public static File tempDir

  private static final Object[] parametersForTest() {
    tempDir = File.createTempDir('ContextTestResolveUriWindowsFiles', '')
    File testFixturesDir = new File(tempDir, 'test-fixtures')
    File someDir = new File(testFixturesDir, 'SomeDir')
    assert someDir.mkdirs()
    new File(someDir, 'myfile.txt').createNewFile()
    // Original Packer code doesn't have toRealPath() (== filepath.EvalSymlinks) call
    // We need it to make drive letter in upper case under Windows
    // Maybe Packer has this problem too
    String nativePathToTestFixtures = testFixturesDir.absoluteFile.toPath().toRealPath().toString()
    String portablePathToTestFixtures = separatorsToUnix(nativePathToTestFixtures)

    [
      // TODO: add different directories
      [
        "$nativePathToTestFixtures\\SomeDir\\myfile.txt",
        "file:///$portablePathToTestFixtures/SomeDir/myfile.txt",
      ],
      [ // without the drive makes this native path a relative file:// uri
        'test-fixtures\\SomeDir\\myfile.txt',
        "file:///$portablePathToTestFixtures/SomeDir/myfile.txt",
      ],
      [ // without the drive makes this native path a relative file:// uri
        'test-fixtures/SomeDir/myfile.txt',
        "file:///$portablePathToTestFixtures/SomeDir/myfile.txt",
      ],
      [ // UNC paths being promoted to smb:// uri scheme.
        "\\\\localhost\\C\$\\$nativePathToTestFixtures\\SomeDir\\myfile.txt",
        "smb://localhost/C\$/$portablePathToTestFixtures/SomeDir/myfile.txt",
      ],
      [ // Absolute uri (incorrect slash type)
        "file:///$nativePathToTestFixtures\\SomeDir\\myfile.txt",
        "file:///$portablePathToTestFixtures/SomeDir/myfile.txt",
      ],
      [ // Absolute uri (existing and mis-spelled)
        "file:///$nativePathToTestFixtures/Somedir/myfile.txt",
        "file:///$portablePathToTestFixtures/SomeDir/myfile.txt",
      ],
      [ // Absolute path (non-existing)
        '\\absolute\\path\\to\\non-existing\\file.txt',
        'file:///absolute/path/to/non-existing/file.txt',
      ],
      [ // Absolute paths (existing)
        "$nativePathToTestFixtures/SomeDir/myfile.txt",
        "file:///$portablePathToTestFixtures/SomeDir/myfile.txt",
      ],
      [ // Relative path (non-existing)
        './nonexisting/relative/path/to/file.txt',
        'file://./nonexisting/relative/path/to/file.txt',
      ],
      [ // Relative path (existing)
        './test-fixtures/SomeDir/myfile.txt',
        "file:///$portablePathToTestFixtures/SomeDir/myfile.txt",
      ],
      [ // Absolute uri (existing and with `/` prefix)
        "file:///$portablePathToTestFixtures/SomeDir/myfile.txt",
        "file:///$portablePathToTestFixtures/SomeDir/myfile.txt",
      ],
      [ // Absolute uri (non-existing and with `/` prefix)
        'file:///path/to/non-existing/file.txt',
        'file:///path/to/non-existing/file.txt',
      ],
      [ // Absolute uri (non-existing and missing `/` prefix)
        'file://path/to/non-existing/file.txt',
        'file://path/to/non-existing/file.txt',
      ],
      [ // Absolute uri and volume (non-existing and with `/` prefix)
        'file:///T:/path/to/non-existing/file.txt',
        'file:///T:/path/to/non-existing/file.txt',
      ],
      [ // Absolute uri and volume (non-existing and missing `/` prefix)
        'file://T:/path/to/non-existing/file.txt',
        'file://T:/path/to/non-existing/file.txt',
      ]
    ]*.toArray().toArray()
  }

  @Test
  @Parameters
  @TestCaseName('resolveUri("{0}") == "{1}"')
  void test(final String original, final String expected) {
    assert new Context(null, null, null, tempDir.toPath()).resolveUri(original).toString() == expected
  }

  @AfterClass
  static void cleanup() {
    tempDir.deleteDir()
  }
}
