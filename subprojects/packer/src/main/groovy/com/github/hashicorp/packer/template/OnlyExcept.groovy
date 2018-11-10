/*
 * OnlyExcept class
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
package com.github.hashicorp.packer.template

import groovy.transform.CompileStatic
import org.gradle.internal.impldep.com.google.common.collect.ImmutableList
import javax.annotation.concurrent.Immutable

// Unlike most other classes, this is immutable as it is used in task arguments too
// Also, note that it is not interpolable and not cloneable TODO
// @KnownImmutable TODO: Groovy 2.5
@Immutable
@CompileStatic
class OnlyExcept {
  final ImmutableList<String> only
  final ImmutableList<String> except

  boolean skip(String n) {
    if (only.empty == false) {
      return !only.contains(n)
    }
    // TOTEST: if
    if (except.empty == false) {
      return except.contains(n)
    }
    false
  }

  int sizeAfterSkip(int originalSize) {
    only?.empty == false ? only.size() : originalSize - except?.size() ?: 0
  }

  OnlyExcept(ImmutableList<String> only, ImmutableList<String> except) {
    this.only = only ?: (ImmutableList<String>)ImmutableList.of()
    this.except = except ?: (ImmutableList<String>)ImmutableList.of()
  }

  static final OnlyExcept only(List<String> only) {
    new OnlyExcept(
      ImmutableList.copyOf(only),
      null
    )
  }

  static final OnlyExcept except(List<String> except) {
    new OnlyExcept(
      null,
      ImmutableList.copyOf(except)
    )
  }

  static final onlyExcept(List<String> only, List<String> except) {
    new OnlyExcept(
      ImmutableList.copyOf(only),
      ImmutableList.copyOf(except)
    )
  }
}
