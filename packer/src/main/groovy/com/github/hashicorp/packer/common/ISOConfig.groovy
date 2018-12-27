package com.github.hashicorp.packer.common

import com.fasterxml.jackson.annotation.JsonProperty
import org.fidata.packer.engine.types.InterpolableChecksumType
import org.fidata.packer.engine.types.InterpolableInputURI
import org.fidata.packer.engine.types.InterpolableFile
import org.fidata.packer.engine.types.base.InterpolableObject
import org.fidata.packer.engine.types.InterpolableString
import groovy.transform.CompileStatic
import org.gradle.api.tasks.Internal
import org.gradle.api.tasks.Nested

@CompileStatic
class ISOConfig extends InterpolableObject {
  InterpolableString isoChecksum

  InterpolableString isoChecksumUrl

  InterpolableChecksumType isoChecksumType

  @Nested
  InterpolableInputURI isoUrls

  @JsonProperty('iso_target_path')
  @Internal
  // TODO: By default will go in the packer cache, with a hash of the original filename as its name
  InterpolableFile targetPath

  @JsonProperty('iso_target_extension')
  @Internal
  InterpolableString targetExtension

  @JsonProperty('iso_url')
  @Nested
  InterpolableInputURI rawSingleISOUrl
}
