package go

import java.util.Objects;
import java.util.StringJoiner;

import static go.Builtin.*;

public final class Strings {
  /**
   * Contains reports whether substr is within s.
   *
   * @param s
   * @param substr
   * @return
   */
  public static boolean contains(String s, String substr) {
    return s.contains(substr);
  }

  /**
   * Join concatenates the elements of a to create a single string. The separator string
   * sep is placed between elements in the resulting string.
   */
  public static String join(String[] a, String sep) {
    return join(a, 0, sep);
  }

  /**
   * Join concatenates the elements of a to create a single string. The separator string
   * sep is placed between elements in the resulting string.
   */
  public static String join(String[] a, int start, String sep) {
    return join(a, start, len(a), sep);
  }

  /**
   * Join concatenates the elements of a to create a single string. The separator string
   * sep is placed between elements in the resulting string.
   */
  public static String join(String[] a, int start, int end, String sep) {
    // Copy of algorithm from String#join(CharSequence, CharSequence...)
    Objects.requireNonNull(sep);
    Objects.requireNonNull(a);
    // Number of elements not likely worth Arrays.stream overhead.
    StringJoiner joiner = new StringJoiner(sep);
    for (int i = start; i < end; i++) {
      joiner.add(a[i]);
    }
    return joiner.toString();
  }

  private Strings() {}
}
