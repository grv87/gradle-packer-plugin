/*
 * Builder class
 * Copyright © 2018  Basil Peace
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
 */
package org.fidata.gradle.packer.template

import com.fasterxml.jackson.annotation.JsonSubTypes
import com.fasterxml.jackson.annotation.JsonTypeInfo
import com.fasterxml.jackson.annotation.JsonUnwrapped
import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.core.JsonProcessingException
import com.fasterxml.jackson.databind.DeserializationContext
import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.deser.std.StdDeserializer
import com.fasterxml.jackson.databind.node.ObjectNode
import com.sun.javaws.exceptions.InvalidArgumentException
import groovy.transform.CompileStatic
import org.fidata.gradle.packer.template.builder.Null
import org.fidata.gradle.packer.template.internal.InterpolableObject
import org.fidata.gradle.packer.template.utils.BuilderHeader
import org.gradle.api.tasks.Nested
import org.omg.CORBA.DynAnyPackage.Invalid

@JsonTypeInfo(
  use = JsonTypeInfo.Id.NAME,
  include = JsonTypeInfo.As.PROPERTY,
  property = 'type'
)
@JsonSubTypes([
  @JsonSubTypes.Type(Null),
])
@CompileStatic
abstract class Builder extends InterpolableObject {
  @JsonUnwrapped
  @Nested
  BuilderHeader header

  @Override
  protected void doInterpolate(Context ctx) {
    header.interpolate ctx
  }

  static TypedDynamicDeserializer
}
