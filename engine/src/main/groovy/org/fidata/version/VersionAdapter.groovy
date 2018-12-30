package org.fidata.version

import com.github.zafarkhaja.semver.Version as SemverVersion
import com.fasterxml.jackson.core.Version as JacksonVersion
import java.nio.file.Paths

final class VersionAdapter {
  private final SemverVersion semverVersion
  private final String groupId
  private final String artifactId
  VersionAdapter(SemverVersion semverVersion, String groupId, String artifactId) {
    this.@semverVersion = semverVersion
    this.@groupId = groupId
    this.@artifactId = artifactId
  }

  /**
   * Returns the major version number.
   *
   * @return the major version number
   */
  int getMajorVersion() {
    return this.semverVersion.majorVersion
  }

  /**
   * Returns the minor version number.
   *
   * @return the minor version number
   */
  int getMinorVersion() {
    this.semverVersion.minorVersion
  }

  /**
   * Returns the patch version number.
   *
   * @return the patch version number
   */
  int getPatchVersion() {
    this.semverVersion.patchVersion
  }

  /**
   * Returns the string representation of the pre-release version.
   *
   * @return the string representation of the pre-release version
   */
  String getPreReleaseVersion() {
    this.semverVersion.preReleaseVersion
  }

  /**
   * Returns the string representation of the build metadata.
   *
   * @return the string representation of the build metadata
   */
  String getBuildMetadata() {
    this.semverVersion.buildMetadata
  }

  /**
   * TODO
   * @return
   */
  String getGroupId() {
    return this.@groupId
  }

  /**
   * TODO
   * @return
   */
  String getArtifactId() {
    return this.@artifactId
  }

  @Lazy
  private JacksonVersion jacksonVersion = new JacksonVersion(
    this.@semverVersion.majorVersion,
    this.@semverVersion.minorVersion,
    this.@semverVersion.patchVersion,
    this.@semverVersion.preReleaseVersion,
    this.@groupId,
    this.@artifactId
  )

  /**
   * Returns this version as SemVer version
   * @return
   */
  SemverVersion asSemver() {
    this.@semverVersion
  }

  JacksonVersion asJackson() {
    this.jacksonVersion
  }

  // TODO: <T> T asType(Class<T> clazz) {
  Object asType(Class clazz) {
    if (clazz == SemverVersion) {
      asSemver()
    } else if (clazz == JacksonVersion) {
      asJackson()
    }
  }

  /**
   * Will attempt to load the maven version for the given groupId and
   * artifactId.  Maven puts a pom.properties file in
   * META-INF/maven/groupId/artifactId, containing the groupId,
   * artifactId and version of the library.
   *
   * This code is moved from {@link com.fasterxml.jackson.core.util.VersionUtil#mavenVersionFor}
   * from jackson-core 2.9.8. The following changes were made:
   * * Handle version string as SemVer (with build metadata)
   * * Exceptions are propagated instead of ignoring
   * * Doesn't return unknown version by default / in the case of exception.
   *
   * @param cl the ClassLoader to load the pom.properties file from
   * @param groupId the groupId of the library
   * @param artifactId the artifactId of the library
   * @return The version
   * @throws IOException if an I/O error occurs
   * @throws IllegalArgumentException if the version in pom.properties file is not found or empty
   * @throws com.github.zafarkhaja.semver.ParseException when version in pom.properties file is invalid
   */
  @SuppressWarnings('resource')
  static VersionAdapter mavenVersionFor(ClassLoader cl, String groupId, String artifactId) throws IOException {
    cl.getResourceAsStream(Paths.get(
      'META-INF/maven',
      groupId.replaceAll('\\.', '/'),
      artifactId,
      'pom.properties'
    ).toString()).withStream { InputStream pomProperties ->
      Properties props = new Properties()
      props.load(pomProperties)
      String versionStr = props.getProperty('version')
      String pomPropertiesArtifactId = props.getProperty('artifactId')
      String pomPropertiesGroupId = props.getProperty('groupId')
      new VersionAdapter(SemverVersion.valueOf(versionStr), pomPropertiesGroupId, pomPropertiesArtifactId)
    }
  }
}
