/*
 * com.github.hashicorp.packer Package Info
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
 */
/**
 * This package contains Java port of Packer template structures.
 * Builders, provisioners and post-processors are ported from Packer.
 * Classes are annotated with Jackson annotations to deserialize from JSON.
 * Serialization back to JSON is also supported.
 * Interpolation (parsing functions and user variables used in templates)
 * is implemented differently from Packer.
 * Classes set Gradle task inputs and outputs for incremental build support
 */
package com.github.hashicorp.packer;
