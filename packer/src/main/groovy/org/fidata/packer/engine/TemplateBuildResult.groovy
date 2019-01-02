package org.fidata.packer.engine

import static java.util.Objects.requireNonNull
import com.github.hashicorp.packer.packer.Artifact
import com.google.common.collect.ImmutableList
import java.util.function.Supplier

final class TemplateBuildResult {
  final List<Artifact> artifacts
  final List<Supplier<Boolean>> upToDateWhen

  TemplateBuildResult(
    List<Artifact> artifacts,
    List<Supplier<Boolean>> upToDateWhen
  ) {
    this.@artifacts = ImmutableList.copyOf(requireNonNull(artifacts))
    this.@upToDateWhen = ImmutableList.copyOf(requireNonNull(upToDateWhen))
  }
}
