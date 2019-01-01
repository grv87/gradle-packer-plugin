package com.github.hashicorp.packer.common

import com.fasterxml.jackson.annotation.JsonProperty
import com.google.common.collect.ImmutableList
import groovy.transform.Internal
import org.fidata.gradle.utils.InputURIWrapper
import org.fidata.packer.engine.annotations.AutoImplement
import org.fidata.packer.engine.annotations.ComputedNested
import org.fidata.packer.engine.annotations.ExtraProcessed
import org.fidata.packer.engine.annotations.Staging
import org.fidata.packer.engine.types.InterpolableChecksumType
import org.fidata.packer.engine.types.InterpolableFile
import org.fidata.packer.engine.types.InterpolableURI
import org.fidata.packer.engine.types.base.InterpolableObject
import org.fidata.packer.engine.types.InterpolableString
import groovy.transform.CompileStatic

@AutoImplement
@CompileStatic
abstract class ISOConfig implements InterpolableObject<ISOConfig> {
  abstract InterpolableString getIsoChecksum()

  abstract InterpolableString getIsoChecksumUrl() // TODO

  abstract InterpolableChecksumType getIsoChecksumType()

  @ExtraProcessed
  abstract List<InterpolableURI> getIsoUrls()

  @ComputedNested
  @Internal
  final List<InputURIWrapper> getIsoUrlsAsInputURIs() {
    ImmutableList.copyOf(isoUrls.collect { InterpolableURI interpolableURI -> new InputURIWrapper(interpolableURI.interpolated) })
  }

  @JsonProperty('iso_target_path')
  @Staging
  // TODO: By default will go in the packer cache, with a hash of the original filename as its name
  abstract InterpolableFile getTargetPath()

  @JsonProperty('iso_target_extension')
  @Staging
  abstract InterpolableString getTargetExtension()

  @JsonProperty('iso_url')
  @ExtraProcessed
  abstract InterpolableURI getRawSingleISOUrl() // TODO: special processing
}
