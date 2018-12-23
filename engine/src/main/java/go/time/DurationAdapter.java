/*
 * Java port of functions for Duration formatting and parsing
 * from go/time package
 * Port is made from version go1.11.1
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
package go.time;

import com.google.common.collect.ImmutableMap;
import com.google.common.primitives.UnsignedLong;
import java.time.Duration;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoUnit;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * Java port of functions for Duration formatting and parsing
 * from {@code go/time} package
 */
@SuppressWarnings({"HardCodedStringLiteral", "CharUsedInArithmeticContext", "UnnecessaryExplicitNumericCast"})
public final class DurationAdapter {
  // Common durations. There is no definition for units of Day or larger
  // to avoid confusion across daylight savings time zone transitions.
  public final static Duration NANOSECOND = Duration.of(1L, ChronoUnit.NANOS);
  public final static Duration MICROSECOND = Duration.of(1L, ChronoUnit.MICROS);
  public final static Duration MILLISECOND = Duration.of(1L, ChronoUnit.MILLIS);
  public final static Duration SECOND = Duration.of(1L, ChronoUnit.SECONDS);
  public final static Duration MINUTE = Duration.of(1L, ChronoUnit.MINUTES);
  public final static Duration HOUR = Duration.of(1L, ChronoUnit.HOURS);

  public final static long NANOSECONDS_PER_MICROSECOND = TimeUnit.MICROSECONDS.toNanos(1L);
  public final static long NANOSECONDS_PER_MILLISECOND = TimeUnit.MILLISECONDS.toNanos(1L);
  public final static long NANOSECONDS_PER_SECOND = TimeUnit.SECONDS.toNanos(1L);
  public final static long SECONDS_PER_MINUTE = TimeUnit.MINUTES.toSeconds(1L);
  public final static long MINUTES_PER_HOUR = TimeUnit.HOURS.toMinutes(1L);

  public final static UnsignedLong NANOSECONDS_PER_MICROSECOND_ULONG = UnsignedLong.valueOf(NANOSECONDS_PER_MICROSECOND);
  public final static UnsignedLong NANOSECONDS_PER_MILLISECOND_ULONG = UnsignedLong.valueOf(NANOSECONDS_PER_MILLISECOND);
  public final static UnsignedLong NANOSECONDS_PER_SECOND_ULONG = UnsignedLong.valueOf(NANOSECONDS_PER_SECOND);
  public final static UnsignedLong SECONDS_PER_MINUTE_ULONG = UnsignedLong.valueOf(SECONDS_PER_MINUTE);
  public final static UnsignedLong MINUTES_PER_HOUR_ULONG = UnsignedLong.valueOf(MINUTES_PER_HOUR);

  private final static long NUMERAL_SYSTEM_BASE = 10L;
  private final static UnsignedLong NUMERAL_SYSTEM_BASE_ULONG = UnsignedLong.valueOf(NUMERAL_SYSTEM_BASE);
  private final static long OVERFLOW_BOUNDARY = Long.MAX_VALUE / NUMERAL_SYSTEM_BASE;

  private static final String TIME_INVALID_DURATION = "time: invalid duration ";

  /**
   * Returns a string representing the duration in the form "72h3m0.5s".
   * Leading zero units are omitted. As a special case, durations less than one
   * second format use a smaller unit (milli-, micro-, or nanoseconds) to ensure
   * that the leading digit is non-zero. The zero duration formats as 0s.
   * @param d duration
   * @return string representing the duration
   */
  public static String string(final Duration d) {
    // Largest time is 2540400h10m10.000000000s
    final StringBuilder buf = new StringBuilder(); // TODO: capacity

    /*
     * Don't use Duration.toNanos() since it causes overflow
     * on values near the end of the range
     */
    final long s = d.getSeconds();
    final UnsignedLong n = UnsignedLong.valueOf(d.getNano());
    final boolean neg = s < 0L;
    UnsignedLong u = neg ? UnsignedLong.valueOf(-s).times(NANOSECONDS_PER_SECOND_ULONG).minus(n) : UnsignedLong.valueOf(s).times(NANOSECONDS_PER_SECOND_ULONG).plus(n);

    if (u.compareTo(NANOSECONDS_PER_SECOND_ULONG) < 0) {
      // Special case: if duration is smaller than a second,
      // use smaller units, like 1.2ms
      final int prec;
      buf.append('s');
      if (u.equals(UnsignedLong.ZERO)) {
        return "0s";
      }
      if (u.compareTo(NANOSECONDS_PER_MICROSECOND_ULONG) < 0) {
        // print nanoseconds
        prec = 0;
        buf.append('n');
      } else if (u.compareTo(NANOSECONDS_PER_MILLISECOND_ULONG) < 0) {
        // print microseconds
        prec = 3;
        // U+00B5 'µ' micro sign == 0xC2 0xB5
        buf.append('µ');
      } else {
        // print milliseconds
        prec = 6;
        buf.append('m');
      }
      u = fmtFrac(buf, u, prec);
      fmtInt(buf, u);
    } else {
      buf.append('s');

      u = fmtFrac(buf, u, 9);

      // u is now integer seconds
      fmtInt(buf, u.mod(SECONDS_PER_MINUTE_ULONG));
      u = u.dividedBy(SECONDS_PER_MINUTE_ULONG);

      // u is now integer minutes
      if (u.compareTo(UnsignedLong.ZERO) > 0) {
        buf.append('m');
        fmtInt(buf, u.mod(MINUTES_PER_HOUR_ULONG));
        u = u.dividedBy(MINUTES_PER_HOUR_ULONG);

        // u is now integer hours
        // Stop at hours because days can be different lengths.
        if (u.compareTo(UnsignedLong.ZERO) > 0) {
          buf.append('h');
          fmtInt(buf, u);
        }
      }
    }

    if (neg) {
      buf.append('-');
    }

    return buf.reverse().toString();
  }

  /**
   * Formats the fraction of v/10**prec (e.g., ".12345") into the
   * tail of buf, omitting trailing zeros.  it omits the decimal
   * point too when the fraction is 0.  It returns the index where the
   * output bytes begin and the value v/10**prec.
   */
  private static UnsignedLong fmtFrac(final StringBuilder buf, UnsignedLong v, final int prec) {
    // Omit trailing zeros up to and including decimal point.
    boolean print = false;
    for (int i = 0; i < prec; i++) {
      final int digit = v.mod(NUMERAL_SYSTEM_BASE_ULONG).intValue();
      print = print || digit != 0;
      if (print) {
        final char c = (char)(digit + '0');
        buf.append(c);
      }
      v = v.dividedBy(NUMERAL_SYSTEM_BASE_ULONG);
    }
    if (print) {
      buf.append('.');
    }
    return v;
  }

  /**
   * Formats v into the tail of buf.
   * It returns the index where the output begins.
   */
  private static void fmtInt(final StringBuilder buf, UnsignedLong v) {
    if (v.equals(UnsignedLong.ZERO)) {
      buf.append('0');
    } else {
      while (v.compareTo(UnsignedLong.ZERO) > 0) {
        final char c = (char)(v.mod(NUMERAL_SYSTEM_BASE_ULONG).intValue() + '0');
        buf.append(c);
        v = v.dividedBy(NUMERAL_SYSTEM_BASE_ULONG);
      }
    }
  }

  private static DateTimeParseException errLeadingInt(CharSequence parsedData, int errorIndex) {
    return new DateTimeParseException("time: bad [0-9]*", parsedData, errorIndex); // never printed
  }

  /**
   * Consumes the leading [0-9]* from s.
   * @throws DateTimeParseException on parse error
   */
  private static Object[] leadingInt(final CharSequence s, final int w) {
    long x = 0L;
    int i;
    for (i = w; i < s.length(); i++){
      final char c = s.charAt(i);
      if (c < '0' || c > '9') {
        break;
      }
      if (x > OVERFLOW_BOUNDARY) {
        // overflow
        throw errLeadingInt(s, w);
      }
      x = x * NUMERAL_SYSTEM_BASE + c - '0';
      if (x < 0L) {
        // overflow
        throw errLeadingInt(s, w);
      }
    }
    return new Object[]{x, i};
  }

  /**
   * Consumes the leading [0-9]* from s.
   * It is used only for fractions, so does not return an error on overflow,
   * it just stops accumulating precision.
   */
  private static Object[] leadingFraction(final CharSequence s, final int w) {
    long x = 0L;
    double scale = 1.0D;
    int i;
    boolean overflow = false;
    for (i = w; i < s.length(); i++){
      final char c = s.charAt(i);
      if (c < '0' || c > '9') {
        break;
      }
      if (overflow) {
        continue;
      }
      if (x > OVERFLOW_BOUNDARY) {
        // It's possible for overflow to give a positive number, so take care.
        overflow = true;
        continue;
      }
      final long y = x * NUMERAL_SYSTEM_BASE + (c - '0');
      if (y < 0L) {
        overflow = true;
        continue;
      }
      x = y;
      scale *= NUMERAL_SYSTEM_BASE;
    }
    return new Object[]{x, scale, i};
  }

  /**
   * Parses a duration string.
   * A duration string is a possibly signed sequence of
   * decimal numbers, each with optional fraction and a unit suffix,
   * such as "300ms", "-1.5h" or "2h45m".
   * Valid time units are "ns", "us" (or "µs"), "ms", "s", "m", "h".
   *
   * @param s duration string
   * @return duration value
   * @throws DateTimeParseException on parse error
   */
  public static Duration parseDuration(final String s) {
    // [-+]?([0-9]*(\.[0-9]*)?[a-z]+)+
    long d = 0L;
    boolean neg = false;
    int w = 0;
    final int l = s.length();

    // Consume [-+]?
    if (!s.isEmpty()) {
      final char c = s.charAt(w);
      if (c == '-' || c == '+') {
        neg = c == '-';
        w++;
      }
    }
    // Special case: if all that is left is "0", this is zero.
    if ("0".equals(s.substring(w))) {
      return Duration.ZERO;
    }
    if (w == l) {
      throw new DateTimeParseException(TIME_INVALID_DURATION + s, s, w);
    }
    Object[] res;
    while (w < l) {
      long v, f = 0L; // integers before, after decimal point
      double scale = 1.0D; // value = v + f/scale

      // The next character must be [0-9.]
      if (!(s.charAt(w) == '.' || '0' <= s.charAt(w) && s.charAt(w) <= '9')) {
        throw new DateTimeParseException(TIME_INVALID_DURATION + s, s, w);
      }
      // Consume [0-9]*
      int pl = w;
      final int w_v = w;
      try {
        res = leadingInt(s, w);
        v = (long) res[0];
        w = (int) res[1];
      } catch (DateTimeParseException e) {
        throw new DateTimeParseException(TIME_INVALID_DURATION + s, s, w_v, e);
      }
      final boolean pre = pl != w; // whether we consumed anything before a period

      // Consume (\.[0-9]*)?
      boolean post = false;
      if (w < l && s.charAt(w) == '.') {
        w++;
        pl = w;
        res = leadingFraction(s, w);
        f = (long)res[0];
        scale = (double)res[1];
        w = (int)res[2];
        post = pl != w;
      }
      if (!pre && !post) {
        // no digits (e.g. ".s" or "-.s")
        throw new DateTimeParseException(TIME_INVALID_DURATION + s, s, w_v);
      }

      // Consume unit.
      int i;
      for (i = w; i < l; i++){
        final char c = s.charAt(i);
        if (c == '.' || '0' <= c && c <= '9') {
          break;
        }
      }
      if (i == w) {
        throw new DateTimeParseException("time: missing unit in duration " + s, s, w);
      }
      final String u = s.substring(w, i);
      final long unit = unitMap.getOrDefault(u, 0L);
      if (unit == 0L) {
        throw new DateTimeParseException("time: unknown unit " + u + " in duration " + s, s, w);
      }
      w = i;
      if (v > Long.MAX_VALUE / unit) {
        // overflow
        throw new DateTimeParseException(TIME_INVALID_DURATION + s, s, w_v);
      }
      v *= unit;
      if (f > 0L) {
        // float64 is needed to be nanosecond accurate for fractions of hours.
        // v >= 0 && (f*unit/scale) <= 3.6e+12 (ns/h, h is the largest unit)
        v += (long)((double)f * (((double)unit) / scale));
        if (v < 0L) {
          // overflow
          throw new DateTimeParseException(TIME_INVALID_DURATION + s, s, w_v);
        }
      }
      d += v;
      if (d < 0L) {
        // overflow
        throw new DateTimeParseException(TIME_INVALID_DURATION + s, s, 0);
      }
    }

    if (neg) {
      d = -d;
    }
    return Duration.ofNanos(d);
  }

  private final static Map<String, Long> unitMap = new ImmutableMap.Builder<String, Long>()
    .put("ns", NANOSECOND.toNanos())
    .put("us", MICROSECOND.toNanos())
    .put("µs", MICROSECOND.toNanos())
    .put("μs", MICROSECOND.toNanos())
    .put("ms", MILLISECOND.toNanos())
    .put("s", SECOND.toNanos())
    .put("m", MINUTE.toNanos())
    .put("h", HOUR.toNanos())
    .build();

  private DurationAdapter() {
    throw new UnsupportedOperationException();
  }
}
