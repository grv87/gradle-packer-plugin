package com.github.hashicorp.packer.packer

import groovy.transform.CompileStatic
import org.gradle.api.file.ConfigurableFileCollection
import org.gradle.api.file.ProjectLayout
import org.gradle.api.tasks.Console
import org.gradle.api.tasks.OutputFiles
import javax.inject.Inject

/**
 * An Artifact is the result of a build, and is the metadata that documents
 * what a builder actually created. The exact meaning of the contents is
 * specific to each builder, but this interface is used to communicate back
 * to the user the result of a build.
 */
@CompileStatic
class Artifact {
  /**
   * Returns the ID of the builder that was used to create this artifact.
   * This is the internal ID of the builder and should be unique to every
   * builder. This can be used to identify what the contents of the
   * artifact actually are.
   */
  final String builderId

  /*@Inject
  private final ProjectLayout projectLayout*/

  /**
   * Returns the set of files that comprise this artifact. If an
   * artifact is not made up of files, then this will be empty.
   */
  @OutputFiles
  final ConfigurableFileCollection files /*= projectLayout.configurableFiles()*/

  /**
   * The ID for the artifact, if it has one. This is not guaranteed to
   * be unique every run (like a GUID), but simply provide an identifier
   * for the artifact that may be meaningful in some way. For example,
   * for Amazon EC2, this value might be the AMI ID.
   */
  final String id

  /**
   * Returns human-readable output that describes the artifact created.
   * This is used for UI output. It can be multiple lines.
   */
  @Console
  final String string

  /**
   * State allows the caller to ask for builder specific state information
   * relating to the artifact instance.
   */
  Object getState(String name) {
    null
  }

  /**
   * Destroy deletes the artifact. Packer calls this for various reasons,
   * such as if a post-processor has processed this artifact and it is
   * no longer needed.
   */
  void destroy() { }
}
