package org.fidata.packer.engine

import static java.util.Objects.requireNonNull
import com.github.hashicorp.packer.packer.Artifact
import com.google.common.collect.ImmutableList
import java.util.function.Supplier

final class PostProcessResult {
  /**
   * Produced artifact. Should not be null. If post-processor doesn't produce new artifact, it should return previous
   * artifact
   */
  final Artifact artifact

  /**
   * True, if post-processor ignores {@code keep_input_artifacts} configuration and always keeps input
   */
  final boolean keep

  /**
   * List of up-to-date checkers, used in addition to artifact.
   * Null is used instead of empty list
   */
  final List<Supplier<Boolean>> upToDateWhen

  /**
   * Whether this post-processor requires interactive mode
   */
  final boolean interactive

  PostProcessResult(
    Artifact artifact,
    boolean keep,
    List<Supplier<Boolean>> upToDateWhen,
    boolean interactive
  ) {
    this.@artifact = requireNonNull(artifact, 'Null artifact, halting post-processor chain.')
    this.@keep = keep
    this.@upToDateWhen = ImmutableList.copyOf(requireNonNull(upToDateWhen))
    this.@interactive = interactive
  }
}
