package go.time;

import java.time.Duration;
import java.time.format.DateTimeParseException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public final class DurationAdapter {
  /**
   * Returns a string representing the duration in the form "72h3m0.5s".
   * Leading zero units are omitted. As a special case, durations less than one
   * second format use a smaller unit (milli-, micro-, or nanoseconds) to ensure
   * that the leading digit is non-zero. The zero duration formats as 0s.
   */
  public static String string(Duration d) {
    // Largest time is 2540400h10m10.000000000s
    StringBuilder buf = new StringBuilder();
    long u = d.getSeconds(); // d.toNanos();
    long s = d.getNano();
    boolean neg = u < 0L;
    if (neg) {
      u = -u;
      s = -s;
    }
    u = u * 1000000000L + s;

    if (u < TimeUnit.SECONDS.toNanos(1)) {
      // Special case: if duration is smaller than a second,
      // use smaller units, like 1.2ms
      int prec;
      buf.append('s');
      if (u == 0L) {
        return "0s";
      }

      if (u < TimeUnit.MICROSECONDS.toNanos(1)) {
        // print nanoseconds
        prec = 0;
        buf.append('n');
      } else if (u < TimeUnit.MILLISECONDS.toNanos(1)) {
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
      fmtInt(buf, u % 60L);
      u /= 60L;

      // u is now integer minutes
      if (u > 0L) {
        buf.append('m');
        fmtInt(buf, u % 60L);
        u /= 60L;

        // u is now integer hours
        // Stop at hours because days can be different lengths.
        if (u > 0L) {
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
  private static long fmtFrac(StringBuilder buf, long v, int prec) {
    // Omit trailing zeros up to and including decimal point.
    boolean print = false;
    for (int i = 0; i < prec; i++) {
      long digit = v % 10L;
      print = print || digit != 0L;
      if (print) {
        char c = (char)(digit + '0');
        buf.append(c);
      }

      v /= 10L;
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
  private static void fmtInt(StringBuilder buf, long v) {
    if (v == 0L) {
      buf.append('0');
    } else {
      while (v > 0L) {
        char c = (char)(v % 10L + '0');
        buf.append(c);
        v /= 10L;
      }

    }

  }

  public static DateTimeParseException errLeadingInt(CharSequence parsedData, int errorIndex) throws DateTimeParseException {
    return new DateTimeParseException("time: bad [0-9]*", parsedData, errorIndex);
  }

  /**
   * Сonsumes the leading [0-9]* from s.
   *
   * @param s
   * @return
   */
  public static Object[] leadingInt(String s, int w) throws DateTimeParseException {
    long x = 0L;
    int i;
    for (i = w; i < s.length(); i++){
      char c = s.charAt(i);
      if (c < '0' || c > '9') {
        break;
      }

      if (x > (1L << 63L - 1L) / 10L) {
        // overflow
        throw errLeadingInt(s, w);
      }

      x = x * 10L + c - '0';
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
  public static Object[] leadingFraction(String s, int w) {
    Long x = 0L;
    Double scale = 1D;
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

      if (x > (1L << 63L - 1L) / 10L) {
        // It's possible for overflow to give a positive number, so take care.
        overflow = true;
        continue;
      }

      long y = x * 10L + c - '0';
      if (y < 0L) {
        overflow = true;
        continue;
      }

      x = y;
      scale *= 10D;
    }

    return new Object[]{x, scale, w};
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
        scale = (int) res[1];
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

      String u = s.substring(w, i - 1);
      long unit;
      try {
        unit = unitMap.get(u);
      } catch (NullPointerException e) {
        throw new DateTimeParseException("time: unknown unit " + u + " in duration " + s, s, w, e);
      }

      w = i;
      if (v > (1L << 63L - 1L) / unit) {
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
