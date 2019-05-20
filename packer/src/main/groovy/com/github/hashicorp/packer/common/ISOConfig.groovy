/*
 * ISOConfig class
 * Copyright ©  Basil Peace
 *
 * This file is part of gradle-packer-plugin.
 *
 * This plugin is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License
 * as published by the Free Software Foundation, either version 3
 * of the License, or (at your option) any later version.
 *
 * This plugin is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this plugin.  If not, see <https://www.gnu.org/licenses/>.
 *
 * Ported from original Packer code,
 * file common/iso_config.go
 * under the terms of the Mozilla Public License, v. 2.0.
 */
package com.github.hashicorp.packer.common

import static org.fidata.utils.ChecksumFileUtils.parseCheckSumFile
import com.fasterxml.jackson.annotation.JsonProperty
import com.github.hashicorp.packer.enums.ChecksumType
import org.codehaus.groovy.runtime.EncodingGroovyMethods
import org.fidata.packer.engine.annotations.AutoImplement
import org.fidata.packer.engine.annotations.ComputedInput
import org.fidata.packer.engine.annotations.ExtraProcessed
import org.fidata.packer.engine.annotations.IgnoreIf
import org.fidata.packer.engine.annotations.Staging
import org.fidata.packer.engine.types.InterpolableChecksumType
import org.fidata.packer.engine.types.InterpolableFile
import org.fidata.packer.engine.types.InterpolableURI
import org.fidata.packer.engine.types.base.InterpolableObject
import org.fidata.packer.engine.types.InterpolableString
import groovy.transform.CompileStatic
import org.fidata.utils.IsoList
import org.fidata.utils.TypedChecksum

@AutoImplement
@CompileStatic
abstract class ISOConfig implements InterpolableObject<ISOConfig> {
  @ExtraProcessed
  abstract InterpolableString getIsoChecksum()

  @ExtraProcessed
  @IgnoreIf({ -> isoChecksum.interpolated })
  abstract InterpolableURI getIsoChecksumUrl() // TODO

  @ExtraProcessed
  abstract InterpolableChecksumType getIsoChecksumType()

  @ExtraProcessed
  abstract List<InterpolableURI> getIsoUrls()

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

  @ComputedInput
  final IsoList getIsoList() {
    List<URI> isoUris = rawSingleISOUrl.interpolated ? [rawSingleISOUrl.interpolated] : isoUrls*.interpolated
    ChecksumType checksumType = isoChecksumType.interpolated
    String checksum = isoChecksum.interpolated // TODO
    if (!checksum && isoChecksumUrl.interpolated) {
      checksum = parseCheckSumFile(isoChecksumUrl.interpolated, isoUris[0], checksumType)
    }
    new IsoList(new TypedChecksum(EncodingGroovyMethods.decodeHex(checksum), checksumType), isoUris)
  }
}
