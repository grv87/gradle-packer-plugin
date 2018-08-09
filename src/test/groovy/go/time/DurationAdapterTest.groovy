package go.time

import groovy.transform.CompileStatic
import junitparams.naming.TestCaseName
import org.junit.Ignore
import org.junit.Test
import junitparams.JUnitParamsRunner
import org.junit.runner.RunWith
import junitparams.Parameters

import java.time.Duration

@RunWith(JUnitParamsRunner)
@CompileStatic
final class DurationAdapterTest {
  static final Object[] durationTests() {
    [
      ["0s", Duration.ZERO],
      ["1ns", Duration.ofNanos(1)],
      ["1.1µs", Duration.ofNanos(1100)],
      ["2.2ms", Duration.ofNanos(2200 * 1000)],
      ["3.3s", Duration.ofMillis(3300)],
      ["4m5s", Duration.ofMinutes(4) + Duration.ofSeconds(5)],
      ["4m5.001s", Duration.ofMinutes(4) + Duration.ofMillis(5001)],
      ["5h6m7.001s", Duration.ofHours(5) + Duration.ofMinutes(6) + Duration.ofMillis(7001)],
      ["8m0.000000001s", Duration.ofMinutes(8) + Duration.ofNanos(1)],
      ["2562047h47m16.854775807s", Duration.ofNanos(/*(1L << 63) - 1L*/Long.MAX_VALUE)],
      ["-2562047h47m16.854775808s", Duration.ofNanos(/*-1L << 63*/Long.MIN_VALUE)],
    ].collect { it.toArray() }.toArray()
  }

  @Test
  @Parameters(method = 'durationTests')
  @TestCaseName('string("{1}") = {0}')
  void testString(final String expected, final Duration d) {
    assert DurationAdapter.string(d) == expected
    if (d.toNanos() > 0) {
      assert DurationAdapter.string(Duration.ofNanos(-d.toNanos())) == "-$expected"
    }
  }

  @Ignore
  @Test
  @Parameters(method = 'durationTests')
  @TestCaseName('testParseDuration("{0}") = {1}')
  void testParseDuration(final String s, final Duration expected) {
    assert DurationAdapter.parseDuration(s) == expected
    if (expected.toNanos() > 0) {
      assert DurationAdapter.parseDuration("-$s") == Duration.ofNanos(-expected.toNanos())
    }
  }
}