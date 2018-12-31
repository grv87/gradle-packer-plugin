package com.github.hashicorp.packer.common

import com.fasterxml.jackson.annotation.JsonProperty
import org.fidata.packer.engine.annotations.AutoImplement
import org.fidata.packer.engine.annotations.Staging
import org.fidata.packer.engine.types.InterpolableChecksumType
import org.fidata.packer.engine.types.InterpolableInputURI
import org.fidata.packer.engine.types.InterpolableFile
import org.fidata.packer.engine.types.base.InterpolableObject
import org.fidata.packer.engine.types.InterpolableString
import groovy.transform.CompileStatic
import org.gradle.api.tasks.Nested

@AutoImplement
@CompileStatic
abstract class ISOConfig implements InterpolableObject<ISOConfig> {
  abstract InterpolableString getIsoChecksum()

  abstract InterpolableString getIsoChecksumUrl() // TODO

  abstract InterpolableChecksumType getIsoChecksumType()

  @Nested
  abstract List<InterpolableInputURI> getIsoUrls()

  @JsonProperty('iso_target_path')
  @Staging
  // TODO: By default will go in the packer cache, with a hash of the original filename as its name
  abstract InterpolableFile getTargetPath()

  @JsonProperty('iso_target_extension')
  @Staging
  abstract InterpolableString getTargetExtension()

  @JsonProperty('iso_url')
  @Nested
  abstract InterpolableInputURI getRawSingleISOUrl()
}
