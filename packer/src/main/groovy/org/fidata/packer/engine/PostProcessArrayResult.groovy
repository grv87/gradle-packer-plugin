package org.fidata.packer.engine

import static java.util.Objects.requireNonNull
import com.github.hashicorp.packer.packer.Artifact
import com.google.common.collect.ImmutableList
import java.util.function.Supplier

final class PostProcessArrayResult {
  final List<Artifact> artifacts
  final boolean keep
  final List<Supplier<Boolean>> upToDateWhen
  final boolean interactive

  PostProcessArrayResult(
    List<Artifact> artifacts,
    boolean keep,
    List<Supplier<Boolean>> upToDateWhen,
    boolean interactive
  ) {
    this.@artifacts = ImmutableList.copyOf(requireNonNull(artifacts))
    this.@keep = keep
    this.@upToDateWhen = ImmutableList.copyOf(requireNonNullupToDateWhen)
    this.@interactive = interactive
  }

  PostProcessArrayResult(
    PostProcessResult postProcessResult
  ) {
    this.@artifacts = ImmutableList.of(postProcessResult.artifact)
    this.@keep = postProcessResult.keep
    this.@upToDateWhen = postProcessResult.upToDateWhen // already immutable
    this.@interactive = postProcessResult.interactive
  }
}
