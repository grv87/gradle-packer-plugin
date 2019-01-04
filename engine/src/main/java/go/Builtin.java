package go;

import java.util.Collection;
import java.util.Map;

/**
 * Package builtin provides documentation for Go's predeclared identifiers.
 * The items documented here are not actually in package builtin
 * but their descriptions here allow godoc to present documentation
 * for the language's special identifiers.
 *
 * Usage:
 * {@code import static go.Builtin.*}
 *
 * Differences with Go:
 * * len function: // TODOC: fix links, format code etc.
 *   * (Pointer to) array: if v is null, {@link NullPointerException} is thrown.
 *     In Go len(v) returns actual (declared) number of elements
 *   * String: the number of Unicode code units in v (the same as {@link String#length()}).
 *     In Go len(v) returns the number of bytes
 *   * Channel: not implemented
 */
public final class Builtin {
  /**
   * The copy built-in function copies elements from a source slice into a
   * destination slice. (As a special case, it also will copy chars from a
   * string to a slice of chars.) The source and destination may overlap. Copy
   * returns the number of elements copied, which will be the minimum of
   * len(src) and len(dst).
   *
   * @param dst
   * @param src
   * @return
   * @throws NullPointerException if src or dst is null
   */
  public static int copy(char[] dst, String src) {
    return copy(dst, src, 0);
  }

  /**
   * The copy built-in function copies elements from a source slice into a
   * destination slice. (As a special case, it also will copy chars from a
   * string to a slice of chars.) The source and destination may overlap. Copy
   * returns the number of elements copied, which will be the minimum of
   * len(src) and len(dst).
   *
   * @param dst
   * @param src
   * @param dstBegin
   * @return
   * @throws NullPointerException if src or dst is null
   */
  public static int copy(char[] dst, String src, int dstBegin) {
    int length = src.length();
    src.getChars(0, length, dst, dstBegin);
    return length;
  }

  /**
   * The len built-in function returns the length of v, according to its type.
   *
   * @param v Array
   * @return The number of elements in v
   * @throws NullPointerException If v is null
   */
  public static int len(boolean[] v) {
    return v.length;
  }

  /**
   * The len built-in function returns the length of v, according to its type.
   *
   * @param v Array
   * @return The number of elements in v
   * @throws NullPointerException If v is null
   */
  public static int len(char[] v) {
    return v.length;
  }

  /**
   * The len built-in function returns the length of v, according to its type.
   *
   * @param v Array
   * @return The number of elements in v
   * @throws NullPointerException If v is null
   */
  public static int len(byte[] v) {
    return v.length;
  }

  /**
   * The len built-in function returns the length of v, according to its type.
   *
   * @param v Array
   * @return The number of elements in v
   * @throws NullPointerException If v is null
   */
  public static int len(short[] v) {
    return v.length;
  }

  /**
   * The len built-in function returns the length of v, according to its type.
   *
   * @param v Array
   * @return The number of elements in v
   * @throws NullPointerException If v is null
   */
  public static int len(int[] v) {
    return v.length;
  }

  /**
   * The len built-in function returns the length of v, according to its type.
   *
   * @param v Array
   * @return The number of elements in v
   * @throws NullPointerException If v is null
   */
  public static int len(long[] v) {
    return v.length;
  }

  /**
   * The len built-in function returns the length of v, according to its type.
   *
   * @param v Array
   * @return The number of elements in v
   * @throws NullPointerException If v is null
   */
  public static int len(float[] v) {
    return v.length;
  }

  /**
   * The len built-in function returns the length of v, according to its type.
   *
   * @param v Array
   * @return The number of elements in v
   * @throws NullPointerException If v is null
   */
  public static int len(double[] v) {
    return v.length;
  }

  /**
   * The len built-in function returns the length of v, according to its type.
   *
   * @param v Array
   * @return The number of elements in v
   * @throws NullPointerException If v is null
   */
  public static int len(Object[] v) {
    return v.length;
  }

  /**
   * The len built-in function returns the length of v, according to its type.
   *
   * @param v Collection
   * @return The number of Unicode code units in v. If v is null, len(v) is zero.
   */
  public static int len(Collection v) {
    return v == null ? 0 : v.size();
  }

  /**
   * The len built-in function returns the length of v, according to its type.
   *
   * @param v Map
   * @return The number of elements in v; if v is null, len(v) is zero.
   */
  public static int len(Map v) {
    return v == null ? 0 : v.size();
  }

  /**
   * The len built-in function returns the length of v, according to its type.
   *
   * @param v String
   * @return The number of Unicode code units in v
   * @throws NullPointerException If v is null
   */
  public static int len(String v) {
    return v.length();
  }

  public static String string(char c) {
    return String.valueOf(c);
  }

  private Builtin() {}
}
