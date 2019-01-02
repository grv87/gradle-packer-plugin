package org.fidata.utils

import com.github.hashicorp.packer.enums.ChecksumType
import java.nio.file.Path
import java.nio.file.Paths

class ChecksumFileUtils {
  /**
   *
   * Ported method parseCheckSumFile
   * from Packer
   * file common/iso_config.go
   *
   * TOTEST
   *
   * @param checksumFileUri
   * @param checksumedFileUri
   *
   * @throws NotFoundException
   */
  static final String parseCheckSumFile(URI checksumFileUri, URI checksumedFileUri, ChecksumType checksumType) {
    Path absPath = Paths.get(checksumedFileUri).toAbsolutePath()
    Path relpath = Paths.get(checksumFileUri).relativize(absPath)
    Path filename = Paths.get(checksumedFileUri).fileName
    List<Path> options = [filename, relpath, Paths.get('./').resolve(relpath), absPath]
    try (Reader reader = checksumFileUri.toURL().newReader()) {
      String line
      while ((line = reader.readLine()) != null) {
        List<String> parts = line.tokenize(' ')
        if (parts.size() >= 2) {
          if (parts[0].toLowerCase() == checksumType.toString()) {
            // BSD-style checksum
            if (options.any { Path match ->
              parts[1] == "($match)"
            }) {
              return parts[3]
            }
          } else {
            // Standard checksum
            if (parts[1][0] == '*') {
              // Binary mode
              parts[1] = parts[1][1..-1]
            }
            if (options.any { Path match ->
              parts[1] == match.toString()
            }) {
              return parts[0]
            }
          }
        }
      }
    }
    throw new NotFoundException(filename, relpath, Paths.get(checksumedFileUri), checksumFileUri)
  }

  static final class NotFoundException extends RuntimeException {
    NotFoundException(Path filename, Path relpath, Path path, URI isoChecksumURL) {
      super(String.sprintf('No checksum for %s, %s or %s found at: %s',
        filename.toString().inspect(),
        relpath.toString().inspect(),
        path.toString().inspect(),
        isoChecksumURL
      ))
    }
  }

  private ChecksumFileUtils() {}
}
