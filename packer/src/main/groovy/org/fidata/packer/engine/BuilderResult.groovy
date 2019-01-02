package org.fidata.packer.engine

import static java.util.Objects.requireNonNull
import com.github.hashicorp.packer.packer.Artifact
import com.google.common.collect.ImmutableList
import java.util.function.Supplier

final class BuilderResult {
  /**
   * Produced artifact. Should be not null
   */
  final Artifact artifact

  /**
   * List of up-to-date checkers, used in addition to artifact. Should be not null
   */
  final List<Supplier<Boolean>> upToDateWhen

  /**
   * Number of local CPUs used for build.
   *
   * For cloud builders, if the builder is able to detect
   * that the cloud is run alongside with this build on the same host
   * (e.g. local installation of Eucalyptus)
   * then it should treat used CPUs as local
   * and so return their number from this method.
   * Otherwise, it should be zero.
   *
   * Right now this number is not used for anything.
   * In the future it could be used to limit build parallelism.
   */
  final int localCpusUsed

  BuilderResult(
    Artifact artifact,
    List<Supplier<Boolean>> upToDateWhen,
    int localCpusUsed
  ) {
    this.@artifact = requireNonNull(artifact)
    this.@upToDateWhen = ImmutableList.copyOf(requireNonNull(upToDateWhen))
    this.@localCpusUsed = localCpusUsed
  }
}
