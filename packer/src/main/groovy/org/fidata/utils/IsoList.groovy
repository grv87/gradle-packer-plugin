package org.fidata.utils

import static java.util.Objects.requireNonNull
import com.google.common.collect.ImmutableList
import groovy.transform.KnownImmutable

@KnownImmutable
final class IsoList implements Serializable {
  final TypedChecksum typedChecksum
  final List<URI> isoUris

  IsoList(
    TypedChecksum typedChecksum,
    List<URI> isoUris
  ) {
    this.@typedChecksum = requireNonNull(typedChecksum)
    this.@isoUris = ImmutableList.copyOf(requireNonNull(isoUris))
  }

  @Override
  boolean equals(Object other) {
    if (other == null) return false
    if (this.is(other)) return true
    if (IsoList.isInstance(other)) {
      IsoList otherSerializableISOList = (IsoList)other
      return this.@typedChecksum == otherSerializableISOList.@typedChecksum &&
        !this.@isoUris.intersect(otherSerializableISOList.@isoUris).empty
    }
    false
  }

  @Override
  int hashCode() {
    this.@typedChecksum.hashCode()
  }

  // TODO: Check for not null in readObject
}
