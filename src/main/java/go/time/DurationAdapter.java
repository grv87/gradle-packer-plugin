/*
 * Java port of functions for Duration formatting and parsing
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
package go.time;

import com.google.common.primitives.UnsignedLong;

import java.time.Duration;
import java.time.format.DateTimeParseException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public final class DurationAdapter {
  private static UnsignedLong SECONDS_PER_MINUTE = UnsignedLong.valueOf(60);
  private static UnsignedLong MINUTES_PER_SECOND = UnsignedLong.valueOf(60);
  private static UnsignedLong NANOSECONDS_PER_SECOND = UnsignedLong.valueOf(1000000000L);
  private static UnsignedLong NUMERAL_SYSTEM_BASE = UnsignedLong.valueOf(10);

  /**
   * Returns a string representing the duration in the form "72h3m0.5s".
   * Leading zero units are omitted. As a special case, durations less than one
   * second format use a smaller unit (milli-, micro-, or nanoseconds) to ensure
   * that the leading digit is non-zero. The zero duration formats as 0s.
   */
  public static String string(Duration d) {
    // Largest time is 2540400h10m10.000000000s
    StringBuilder buf = new StringBuilder();
    long s = d.getSeconds(); // d.toNanos();
    long n = d.getNano();
    UnsignedLong u;
    boolean neg = s < 0L;
    if (neg) {
      u = UnsignedLong.valueOf(-s).times(NANOSECONDS_PER_SECOND).minus(UnsignedLong.valueOf(n));
    } else {
      u = UnsignedLong.valueOf(s).times(NANOSECONDS_PER_SECOND).plus(UnsignedLong.valueOf(n));
    }

    if (u.compareTo(UnsignedLong.valueOf(TimeUnit.SECONDS.toNanos(1))) < 0) {
      // Special case: if duration is smaller than a second,
      // use smaller units, like 1.2ms
      int prec;
      buf.append('s');
      if (u.equals(UnsignedLong.ZERO)) {
        return "0s";
      }

      if (u.compareTo(UnsignedLong.valueOf(TimeUnit.MICROSECONDS.toNanos(1))) < 0) {
        // print nanoseconds
        prec = 0;
        buf.append('n');
      } else if (u.compareTo(UnsignedLong.valueOf(TimeUnit.MILLISECONDS.toNanos(1))) < 0) {
        // print microseconds
        prec = 3;
        // U+00B5 'µ' micro sign == 0xC2 0xB5
        buf.append("µ");
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
      fmtInt(buf, u.mod(SECONDS_PER_MINUTE));
      u = u.dividedBy(SECONDS_PER_MINUTE);

      // u is now integer minutes
      if (u.compareTo(UnsignedLong.ZERO) > 0) {
        buf.append('m');
        fmtInt(buf, u.mod(MINUTES_PER_SECOND));
        u = u.dividedBy(MINUTES_PER_SECOND);

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
   *
   * @return
   */
  private static UnsignedLong fmtFrac(StringBuilder buf, UnsignedLong v, int prec) {
    // Omit trailing zeros up to and including decimal point.
    boolean print = false;
    for (int i = 0; i < prec; i++) {
      int digit = v.mod(NUMERAL_SYSTEM_BASE).intValue();
      print = print || digit != 0L;
      if (print) {
        char c = (char)(digit + '0');
        buf.append(c);
      }

      v = v.dividedBy(NUMERAL_SYSTEM_BASE);
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
  private static void fmtInt(StringBuilder buf, UnsignedLong v) {
    if (v.equals(UnsignedLong.ZERO)) {
      buf.append('0');
    } else {
      while (v.compareTo(UnsignedLong.ZERO) > 0) {
        char c = (char)(v.mod(NUMERAL_SYSTEM_BASE).intValue() + '0');
        buf.append(c);
        v = v.dividedBy(NUMERAL_SYSTEM_BASE);
      }

    }

  }

  private static DateTimeParseException errLeadingInt(CharSequence parsedData, int errorIndex) throws DateTimeParseException {
    return new DateTimeParseException("time: bad [0-9]*", parsedData, errorIndex);
  }

  /**
   * Сonsumes the leading [0-9]* from s.
   *
   * @param s
   * @return
   */
  private static Object[] leadingInt(String s, int w) throws DateTimeParseException {
    long x = 0L;
    int i;
    for (i = w; i < s.length(); i++){
      char c = s.charAt(i);
      if (c < '0' || c > '9') {
        break;
      }

      if (x > Long.MAX_VALUE / NUMERAL_SYSTEM_BASE.longValue()) {
        // overflow
        throw errLeadingInt(s, w);
      }

      x = x * NUMERAL_SYSTEM_BASE.longValue() + c - '0';
      if (x < 0L) {
        // overflow
        throw errLeadingInt(s, w);
      }

    }

    return new Object[]{x, i};
  }

  /**
   * Сonsumes the leading [0-9]* from s.
   * It is used only for fractions, so does not return an error on overflow,
   * it just stops accumulating precision.
   *
   * @param s
   * @return
   */
  private static Object[] leadingFraction(String s, int w) {
    long x = 0L;
    double scale = 1D;
    String rem;
    int i;
    boolean overflow = false;
    for (i = w; i < s.length(); i++){
      char c = s.charAt(i);
      if (c < '0' || c > '9') {
        break;
      }

      if (overflow) {
        continue;
      }

      if (x > Long.MAX_VALUE / NUMERAL_SYSTEM_BASE.longValue()) {
        // It's possible for overflow to give a positive number, so take care.
        overflow = true;
        continue;
      }

      long y = x * NUMERAL_SYSTEM_BASE.longValue() + c - '0';
      if (y < 0L) {
        overflow = true;
        continue;
      }

      x = y;
      scale *= NUMERAL_SYSTEM_BASE.doubleValue();
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
   * @param s
   * @return
   */
  public static Duration parseDuration(final String s) {
    // [-+]?([0-9]*(\.[0-9]*)?[a-z]+)+
    long d = 0L;
    boolean neg = false;
    int w = 0;

    // Consume [-+]?
    if (!s.isEmpty()) {
      char c = s.charAt(w);
      if (c == '-' || c == '+') {
        neg = c == '-';
        w++;
      }

    }

    // Special case: if all that is left is "0", this is zero.
    if (s.substring(w).equals("0")) {
      return Duration.ZERO;
    }

    if (w == s.length()) {
      throw new DateTimeParseException("time: invalid duration " + s, s, w);
    }

    while (w < s.length()) {
      long v;
      long f = 0L;// integers before, after decimal point
      double scale = 1D;// value = v + f/scale

      // The next character must be [0-9.]
      if (!(s.charAt(w) == '.' || '0' <= s.charAt(w) && s.charAt(w) <= '9')) {
        throw new DateTimeParseException("time: invalid duration " + s, s, w);
      }

      // Consume [0-9]*
      int pl = w;
      int w_v = w;
      try {
        Object[] res = leadingInt(s, w);
        v = (long) res[0];
        w = (int) res[1];
      } catch (DateTimeParseException e) {
        throw new DateTimeParseException("time: invalid duration " + s, s, w_v, e);
      }

      boolean pre = pl != w;// whether we consumed anything before a period

      // Consume (\.[0-9]*)?
      boolean post = false;
      if (w < s.length() && s.charAt(w) == '.') {
        w++;
        pl = w;
        Object[] res = leadingFraction(s, w);
        f = (long) res[0];
        scale = (double) res[1];
        w = (int) res[2];
        post = pl != w;
      }

      if (!pre && !post) {
        // no digits (e.g. ".s" or "-.s")
        throw new DateTimeParseException("time: invalid duration " + s, s, w_v);
      }


      // Consume unit.
      int i;
      for (i = w; i < s.length(); i++){
        char c = s.charAt(i);
        if (c == '.' || '0' <= c && c <= '9') {
          break;
        }

      }

      if (i == w) {
        throw new DateTimeParseException("time: missing unit in duration " + s, s, w);
      }

      String u = s.substring(w, i);
      long unit;
      try {
        unit = unitMap.get(u);
      } catch (NullPointerException e) {
        throw new DateTimeParseException("time: unknown unit " + u + " in duration " + s, s, w, e);
      }

      w = i;
      if (v > Long.MAX_VALUE / unit) {
        // overflow
        throw new DateTimeParseException("time: invalid duration " + s, s, w_v);
      }

      v *= unit;
      if (f > 0L) {
        // float64 is needed to be nanosecond accurate for fractions of hours.
        // v >= 0 && (f*unit/scale) <= 3.6e+12 (ns/h, h is the largest unit)
        v += (long) ((double) f * (((double) unit) / scale));
        if (v < 0L) {
          // overflow
          throw new DateTimeParseException("time: invalid duration " + s, s, w_v);
        }

      }

      d += v;
      if (d < 0L) {
        // overflow
        throw new DateTimeParseException("time: invalid duration " + s, s, 0);
      }

    }


    if (neg) {
      d = -d;
    }

    return Duration.ofNanos(d);
  }

  private DurationAdapter() {
    throw new AssertionError();// TODO
  }

  private final static Map<String, Long> unitMap = new HashMap<String, Long>(8);

  static {
    unitMap.put("ns", TimeUnit.NANOSECONDS.toNanos(1));
    unitMap.put("us", TimeUnit.MICROSECONDS.toNanos(1));
    unitMap.put("µs", TimeUnit.MICROSECONDS.toNanos(1));
    unitMap.put("μs", TimeUnit.MICROSECONDS.toNanos(1));
    unitMap.put("ms", TimeUnit.MILLISECONDS.toNanos(1));
    unitMap.put("s", TimeUnit.SECONDS.toNanos(1));
    unitMap.put("m", TimeUnit.MINUTES.toNanos(1));
    unitMap.put("h", TimeUnit.HOURS.toNanos(1));
  }

}
