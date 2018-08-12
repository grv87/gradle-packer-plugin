/*
 * Java port of tests for functions for Duration formatting and parsing
 * from go/time package
 * Port is made from version go1.10.3
 * Copyright © 2018  Basil Peace
 * Copyright 2009, 2010 The Go Authors. All rights reserved.
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
package go.time

import org.junit.Ignore

import static org.hamcrest.Matchers.startsWith
import groovy.transform.CompileStatic
import junitparams.naming.TestCaseName
import org.junit.Rule
import org.junit.Test
import junitparams.JUnitParamsRunner
import org.junit.rules.ExpectedException
import org.junit.runner.RunWith
import junitparams.Parameters
import java.time.Duration
import java.time.format.DateTimeParseException

@RunWith(JUnitParamsRunner)
final class DurationAdapterTest {
  @Rule
  public ExpectedException thrown = ExpectedException.none()

  static final Object[] durationTests() {
    [
      ['0s', Duration.ZERO],
      ['1ns', Duration.ofNanos(1)],
      ['1.1µs', Duration.ofNanos(1100)],
      ['2.2ms', Duration.ofNanos(2200 * 1000)],
      ['3.3s', Duration.ofMillis(3300)],
      ['4m5s', Duration.ofMinutes(4) + Duration.ofSeconds(5)],
      ['4m5.001s', Duration.ofMinutes(4) + Duration.ofMillis(5001)],
      ['5h6m7.001s', Duration.ofHours(5) + Duration.ofMinutes(6) + Duration.ofMillis(7001)],
      ['8m0.000000001s', Duration.ofMinutes(8) + Duration.ofNanos(1)],
      ['2562047h47m16.854775807s', Duration.ofNanos((1L << 63) - 1L /*Long.MAX_VALUE*/)],
      ['-2562047h47m16.854775808s', Duration.ofNanos(-1L << 63 /*Long.MIN_VALUE*/)],
    ].collect { it.toArray() }.toArray()
  }

  @Test
  @Parameters(method = 'durationTests')
  @TestCaseName('string("{1}") == {0}')
  void testString(final String expected, final Duration d) {
    assert DurationAdapter.string(d) == expected
    if (!(d.negative || d.zero)) {
      assert DurationAdapter.string(d.negated()/*Duration.ofSeconds(-d.seconds, -d.nano)*/) == "-$expected"
    }
  }

  static final Object[] parseDurationTests() {
    [
      // simple
      ["0", true, Duration.ZERO],
      ["5s", true, Duration.ofSeconds(5)],
      ["30s", true, Duration.ofSeconds(30)],
      ["1478s", true, Duration.ofSeconds(1478)],
      // sign
      ["-5s", true, Duration.ofSeconds(-5)],
      ["+5s", true, Duration.ofSeconds(5)],
      ["-0", true, Duration.ZERO],
      ["+0", true, Duration.ZERO],
      // decimal
      ["5.0s", true, Duration.ofSeconds(5)],
      ["5.6s", true, Duration.ofSeconds(5) + Duration.ofMillis(600)],
      ["5.s", true, Duration.ofSeconds(5)],
      [".5s", true, Duration.ofMillis(500)],
      ["1.0s", true, Duration.ofSeconds(1)],
      ["1.00s", true, Duration.ofSeconds(1)],
      ["1.004s", true, Duration.ofSeconds(1) + Duration.ofMillis(4)],
      ["1.0040s", true, Duration.ofSeconds(1) + Duration.ofMillis(4)],
      ["100.00100s", true, Duration.ofSeconds(100) + Duration.ofMillis(1)],
      // different units
      ["10ns", true, Duration.ofNanos(10)],
      ["11us", true, Duration.ofNanos(11 * 1000 /* TODO */)],
      ["12µs", true, Duration.ofNanos(12 * 1000 /* TODO */)], // U+00B5
      ["12μs", true, Duration.ofNanos(12 * 1000 /* TODO */)], // U+03BC
      ["13ms", true, Duration.ofMillis(13)],
      ["14s", true, Duration.ofSeconds(14)],
      ["15m", true, Duration.ofMinutes(15)],
      ["16h", true, Duration.ofHours(16)],
      // composite durations
      ["3h30m", true, Duration.ofHours(3) + Duration.ofMinutes(30)],
      ["10.5s4m", true, Duration.ofMinutes(4) + Duration.ofSeconds(10) + Duration.ofMillis(500)],
      ["-2m3.4s", true, (Duration.ofMinutes(2) + Duration.ofSeconds(3) + Duration.ofMillis(400)).negated()],
      ["1h2m3s4ms5us6ns", true, Duration.ofHours(1) + Duration.ofMinutes(2) + Duration.ofSeconds(3) + Duration.ofMillis(4) + Duration.ofNanos(5 * 1000 + 6)],
      ["39h9m14.425s", true, Duration.ofHours(39) + Duration.ofMinutes(9) + Duration.ofSeconds(14) + Duration.ofMillis(425)],
      // large value
      ["52763797000ns", true, Duration.ofNanos(52763797000)],
      // more than 9 digits after decimal point, see https://golang.org/issue/6617
      ["0.3333333333333333333h", true, Duration.ofMinutes(20)],
      // 9007199254740993 = 1<<53+1 cannot be stored precisely in a float64
      ["9007199254740993ns", true, Duration.ofNanos((1L << 53) + 1)],
      // largest duration that can be represented by int64 in nanoseconds
      ["9223372036854775807ns", true, Duration.ofNanos((1L << 63) - 1)],
      ["9223372036854775.807us", true, Duration.ofNanos((1L << 63) - 1)],
      ["9223372036s854ms775us807ns", true, Duration.ofNanos((1L << 63) - 1)],
      // large negative value
      ["-9223372036854775807ns", true, Duration.ofNanos((-1L << 63) + 1)],
      // huge string; issue 15011.
      ["0.100000000000000000000h", true, Duration.ofMinutes(6)],
      // This value tests the first overflow check in leadingFraction.
      ["0.830103483285477580700h", true, Duration.ofMinutes(49) + Duration.ofSeconds(48) + Duration.ofNanos(372539827)],
  
      // errors
      ["", false, null],
      ["3", false, null],
      ["-", false, null],
      ["s", false, null],
      [".", false, null],
      ["-.", false, null],
      [".s", false, null],
      ["+.s", false, null],
      ["3000000h", false, null],                  // overflow
      ["9223372036854775808ns", false, null],     // overflow
      ["9223372036854775.808us", false, null],    // overflow
      ["9223372036854ms775us808ns", false, null], // overflow
      // largest negative value of type int64 in nanoseconds should fail
      // see https://go-review.googlesource.com/#/c/2461/
      ["-9223372036854775808ns", false, null],
    ].collect { (it + [it[1] ? "== ${ it[2] }".toString() : 'throws DateTimeParseException']).toArray() }.toArray()
  }

  @Test
  @Parameters(method = 'parseDurationTests')
  @TestCaseName('parseDuration("{0}") {3}')
  void testParseDuration(final String in_, final boolean ok, final Duration want, final String test) {
    if (ok) {
      assert DurationAdapter.parseDuration(in_) == want
    } else {
      thrown.expect(DateTimeParseException)
      thrown.expectMessage(startsWith('time: '))
      DurationAdapter.parseDuration(in_)
    }
  }

  @Test
  void testParseDurationRoundTrip() {
    Random rand = new Random()
    for(int i = 0; i < 100; i++) {
      // Resolutions finer than milliseconds will result in
      // imprecise round-trips.
      Duration d0 = Duration.ofMillis(rand.nextInt())
      String s = DurationAdapter.string(d0)
      Duration d1 = DurationAdapter.parseDuration(s)
      assert d0.equals(d1) // TOCHECK: Groovy
    }
  }
}