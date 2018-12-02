package org.fidata.gradle.packer.exceptions

class SharedDataNotFound extends IllegalStateException {
  SharedDataNotFound() {
    super('Packer plugin shared data is not found. You should apply org.fidata.packer plugin to Settings too.')
  }
}
