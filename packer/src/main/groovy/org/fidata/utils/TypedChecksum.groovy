package org.fidata.utils

import com.github.hashicorp.packer.enums.ChecksumType
import groovy.transform.Immutable

// TODO: check for nulls ?
@Immutable
final class TypedChecksum implements Serializable {
  byte[] checksum
  ChecksumType type
  // TODO: ignore checksum if type == NONE
}
