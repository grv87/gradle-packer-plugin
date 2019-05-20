/*
 * Artifact trait
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
 *
 * Ported from original Packer code,
 * file packer/artifact.go
 * under the terms of the Mozilla Public License, v. 2.0.
 */
package com.github.hashicorp.packer.packer

import groovy.transform.CompileStatic
import org.gradle.api.tasks.Console
import org.gradle.api.tasks.Internal
import org.gradle.api.tasks.OutputFiles

/**
 * An Artifact is the result of a build, and is the metadata that documents
 * what a builder actually created. The exact meaning of the contents is
 * specific to each builder, but this interface is used to communicate back
 * to the user the result of a build.
 */
@CompileStatic
trait Artifact {
  /**
   * Returns the ID of the builder that was used to create this artifact.
   * This is the internal ID of the builder and should be unique to every
   * builder. This can be used to identify what the contents of the
   * artifact actually are.
   */
  @Internal
  final String builderId

  /**
   * Returns the set of files that comprise this artifact. If an
   * artifact is not made up of files, then this will be empty.
   *
   * CAVEAT:
   * We use map instead of list, it's a requirement from Gradle's build cache
   * <grv87 2019-01-02>
   */
  @OutputFiles
  final Map<String, File> files // MARK2

  /**
   * The ID for the artifact, if it has one. This is not guaranteed to
   * be unique every run (like a GUID), but simply provide an identifier
   * for the artifact that may be meaningful in some way. For example,
   * for Amazon EC2, this value might be the AMI ID.
   */
  @Internal
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
