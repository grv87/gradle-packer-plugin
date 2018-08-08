
package go.time

import java.text.ParseException
import java.time.Duration
import java.time.format.DateTimeParseException
import java.time.temporal.ChronoUnit

final class DurationAdapter {
  /**
   * Returns a string representing the duration in the form "72h3m0.5s".
   * Leading zero units are omitted. As a special case, durations less than one
   * second format use a smaller unit (milli-, micro-, or nanoseconds) to ensure
   * that the leading digit is non-zero. The zero duration formats as 0s.
   */
  static String string(Duration d) {
    // Largest time is 2540400h10m10.000000000s
    var buf [32]byte
    w := len(buf)

    u := uint64(d)
    neg := d < 0
    if neg {
      u = -u
    }

    if u < uint64(Second) {
      // Special case: if duration is smaller than a second,
      // use smaller units, like 1.2ms
      var prec int
      w--
      buf[w] = 's'
      w--
      switch {
        case u == 0:
        return "0s"
        case u < uint64(Microsecond):
        // print nanoseconds
        prec = 0
        buf[w] = 'n'
        case u < uint64(Millisecond):
        // print microseconds
        prec = 3
        // U+00B5 'µ' micro sign == 0xC2 0xB5
        w-- // Need room for two bytes.
        copy(buf[w:], "µ")
        default:
        // print milliseconds
        prec = 6
        buf[w] = 'm'
      }
      w, u = fmtFrac(buf[:w], u, prec)
      w = fmtInt(buf[:w], u)
    } else {
      w--
      buf[w] = 's'

      w, u = fmtFrac(buf[:w], u, 9)

      // u is now integer seconds
      w = fmtInt(buf[:w], u%60)
      u /= 60

      // u is now integer minutes
      if u > 0 {
        w--
        buf[w] = 'm'
        w = fmtInt(buf[:w], u%60)
        u /= 60

        // u is now integer hours
        // Stop at hours because days can be different lengths.
        if u > 0 {
          w--
          buf[w] = 'h'
          w = fmtInt(buf[:w], u)
        }
      }
    }

    if neg {
      w--
      buf[w] = '-'
    }

    return string(buf[w:])
  }

/**
 * fmtFrac formats the fraction of v/10**prec (e.g., ".12345") into the
 * tail of buf, omitting trailing zeros.  it omits the decimal
 * point too when the fraction is 0.  It returns the index where the
 * output bytes begin and the value v/10**prec.
 * @return
 */
  private fmtFrac(buf []byte, v uint64, prec int) (nw int, nv uint64) {
    // Omit trailing zeros up to and including decimal point.
    w := len(buf)
    print := false
    for i := 0; i < prec; i++ {
      digit := v % 10
      print = print || digit != 0
      if print {
        w--
        buf[w] = byte(digit) + '0'
      }
      v /= 10
    }
    if print {
      w--
      buf[w] = '.'
    }
    return w, v
  }

  /**
   * Formats v into the tail of buf.
   * It returns the index where the output begins.
   */
  private fmtInt(buf []byte, v uint64) int {
    w := len(buf)
    if v == 0 {
      w--
      buf[w] = '0'
    } else {
      for v > 0 {
        w--
        buf[w] = byte(v%10) + '0'
        v /= 10
      }
    }
    return w
  }

  static Exception errLeadingInt() {
    new DateTimeParseException('time: bad [0-9]*')
  } // never printed

  /**
   * Сonsumes the leading [0-9]* from s.
   * @param s
   * @return
   */
  static Tuple2<Long, String> leadingInt(String s) {
    Long x = 0
    int i
    for (i = 0; i < s.length(); i++) {
      char c = s.charAt(i)
      if (c < ('0' as char) || c > ('9' as char)) {
        break
      }
      if (x > (1<<63-1)/10) {
        // overflow
        throw errLeadingInt()
      }
      x = x*10 + Long.parseLong(c.toString()) - '0' // ???
      if (x < 0) {
        // overflow
        throw errLeadingInt()
      }
    }
    return new Tuple2(x, s[i..-1])
  }

  /**
   * Сonsumes the leading [0-9]* from s.
   * It is used only for fractions, so does not return an error on overflow,
   * it just stops accumulating precision.
   * @param s
   * @return
   */
  static Tuple2<Tuple2<Long, Double>, String> leadingFraction(String s) {
    Long x = 0
    Double scale = 1
    String rem
    int i
    boolean overflow = false
    for (i = 0; i < s.length(); i++) {
      char c = s.charAt(i)
      if (c < ('0' as char) || c > ('9' as char)) {
        break
      }
      if (overflow) {
        continue
      }
      if (x > (1<<63-1)/10) {
        // It's possible for overflow to give a positive number, so take care.
        overflow = true
        continue
      }
      Long y = x*10 + Long.parseLong(c.toString()) - '0' // ??
      if (y < 0) {
        overflow = true
        continue
      }
      x = y
      scale *= 10
    }
    return new Tuple2(new Tuple2(x, scale), s[i..-1])
  }

  static Map<String, ChronoUnit> unitMap = [
    "ns": ChronoUnit.NANOS,
    "us": ChronoUnit.MICROS,
    "µs": ChronoUnit.MICROS, // U+00B5 = micro symbol
    "μs": ChronoUnit.MICROS, // U+03BC = Greek letter mu
    "ms": ChronoUnit.MILLIS,
    "s":  ChronoUnit.SECONDS,
    "m":  ChronoUnit.MINUTES,
    "h":  ChronoUnit.HOURS,
  ]

  /**
   * Parses a duration string.
   * A duration string is a possibly signed sequence of
   * decimal numbers, each with optional fraction and a unit suffix,
   * such as "300ms", "-1.5h" or "2h45m".
   * Valid time units are "ns", "us" (or "µs"), "ms", "s", "m", "h".
   * @param s
   * @return
   */
  static Duration parseDuration(String s) {
    // [-+]?([0-9]*(\.[0-9]*)?[a-z]+)+
    String orig = s
    new StringReader(s).withReader { Reader r ->
      Long d
      boolean neg = false

      // Consume [-+]?
      if (!s.empty) {
        char c = s.charAt(0)
        if (c == ('-' as char) || c == ('+' as char)) {
          neg = c == ('-' as char)
          s = s[1..-1]
        }
      }
      // Special case: if all that is left is "0", this is zero.
      if (s == "0") {
        return Duration.ZERO
      }
      if (s.empty) {
        throw new DateTimeParseException("time: invalid duration " + orig)
      }
      while (!s.empty) {
        long v, f // integers before, after decimal point
        double scale = 1 // value = v + f/scale

        // The next character must be [0-9.]
        if (!(s[0] == '.' || '0' <= s[0] && s[0] <= '9')) {
          throw new DateTimeParseException("time: invalid duration " + orig)
        }
        // Consume [0-9]*
        int pl = s.length()
        try {
          (v, s) = leadingInt(s)
        } catch {
          throw new DateTimeParseException("time: invalid duration " + orig)
        }
        bool pre = pl != s.length() // whether we consumed anything before a period

        // Consume (\.[0-9]*)?
        bool post = false
        if (!s.empty && s.charAr(0) == ('.' as char)) {
          s = s[1..-1]
          pl = s.length()
          (f, scale, s) = leadingFraction(s)
          post = pl != s.length()
        }
        if (!pre && !post) {
          // no digits (e.g. ".s" or "-.s")
          throw new DateTimeParseException("time: invalid duration " + orig)
        }

        // Consume unit.
        int i
        for (i = 0; i < s.length(); i++) {
          char c = s.charAt(i)
          if (c == ('.' as char) || ('0' as char) <= c && c <= ('9' as char)) {
            break
          }
        }
        if (i == 0) {
          throw new DateTimeParseException("time: missing unit in duration " + orig)
        }
        u = s[0..i]
        s = s[i..-1]
        try {
          ChronoUnit unit = unitMap[u]
        } catch { // TODO
          throw new DateTimeParseException("time: unknown unit " + u + " in duration " + orig)
        }
        if (v > (1 << 63 - 1) / unit) {
          // overflow
          throw new DateTimeParseException("time: invalid duration " + orig)
        }
        v *= unit
        if (f > 0) {
          // float64 is needed to be nanosecond accurate for fractions of hours.
          // v >= 0 && (f*unit/scale) <= 3.6e+12 (ns/h, h is the largest unit)
          v += int64(float64(f) * (float64(unit) / scale))
          if (v < 0) {
            // overflow
            throw new DateTimeParseException("time: invalid duration " + orig)
          }
        }
        d += v
        if (d < 0) {
          // overflow
          throw new DateTimeParseException("time: invalid duration " + orig)
        }
      }

      if (neg) {
        d = -d
      }
      Duration.ofNanos(d)
    }
  }

  // Suppress default constructor for noninstantiability
  private DurationClass() {
    throw new AssertionError() // TODO
  }
}
