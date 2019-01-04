package go.path.filepath;

import static go.Builtin.*;
import static go.Runtime.GoOS.*;

import go.Os;
import go.Strings;
import go.strings.Utf8;
import go.Runtime;
import groovy.lang.Tuple2;
import groovy.lang.Tuple3;
import org.apache.commons.lang3.StringUtils;

import java.util.List;

final class FilePath {
  public static final char SEPARATOR = Os.PATH_SEPARATOR;

  public static class ErrBadPattern extends RuntimeException { // TOTHINK
    ErrBadPattern() {
      this(null);
    }
    ErrBadPattern(Throwable cause) {
      super("syntax error in pattern", cause);
    }
  }

  /**
   * match reports whether name matches the shell file name pattern.
   * The pattern syntax is:
   *
   * pattern:
   * 	{ term }* //	term:
   * 	'*'         matches any sequence of non-Separator characters
   * 	'?'         matches any single non-Separator character
   * 	'[' [ '^' ] { character-range } ']'
   * 	            character class (must be non-empty)
   * 	c           matches character c (c != '*', '?', '\\', '[')
   * 	'\\' c      matches character c
   *
   * character-range:
   * 	c           matches character c (c != '\\', '-', ']')
   * 	'\\' c      matches character c
   * 	lo '-' hi   matches character c for lo <= c <= hi
   *
   * Match requires pattern to match all of name, not just a substring.
   * The only possible returned error is ErrBadPattern, when pattern
   * is malformed.
   *
   * On Windows, escaping is disabled. Instead, '\\' is treated as
   * path separator.
   *
   * @param pattern
   * @param name
   * @return
   */
  public boolean match(String pattern, String name) {
    pattern:
    while (len(pattern) > 0) {
      Tuple3<Boolean, String, String> scanChunkResult = scanChunk(pattern);
      boolean star = scanChunkResult.getFirst();
      String chunk = scanChunkResult.getSecond();
      pattern = scanChunkResult.getThird();
      if (star && chunk.isEmpty()) {
        // Trailing * matches rest of string unless it has a /.
        return !Strings.contains(name, string(SEPARATOR));
      }
      // Look for match at current position.
      Tuple2<String, Boolean> matchChunkResult = matchChunk(chunk, name);
      String t = matchChunkResult.getFirst();
      boolean ok = matchChunkResult.getSecond();
      // if we're the last chunk, make sure we've exhausted the name
      // otherwise we'll give a false result even if we could still match
      // using the star
      if (ok && (len(t) == 0 || len(pattern) > 0)) {
        name = t;
        continue;
      }
      if (star) {
        // Look for match skipping i+1 bytes.
        // Cannot skip /.
        for (int i = 0; i < len(name) && name.charAt(i) != SEPARATOR; i++) {
          matchChunkResult = matchChunk(chunk, name.substring(i + 1));
          t = matchChunkResult.getFirst();
          ok = matchChunkResult.getSecond();
          if (ok) {
            // if we're the last chunk, make sure we exhausted the name
            if (len(pattern) == 0 && len(t) > 0) {
              continue;
            }
            name = t;
            continue pattern;
          }
        }
      }
      return false;
    }
    return len(name) == 0;
  }

  /**
   * scanChunk gets the next segment of pattern, which is a non-star string
   * possibly preceded by a star.
   *
   * @param pattern
   * @return Tuple of (star, chunk, rest)
   */
  private Tuple3<Boolean, String, String> scanChunk(String pattern) {
    int i = 0;
    boolean star = false;
    int length = len(pattern);
    while (i < length && pattern.charAt(i) == '*') {
      i++;
      star = true;
    }
    boolean inrange = false;
    scan:
    for(; i < length; i++) {
      switch (pattern.charAt(i)) {
        case '\\':
          if (Runtime.GOOS != WINDOWS) {
            // error check handled in matchChunk: bad pattern.
            if (i + 1 < length) {
              i++;
            }
          }
          break;
        case '[':
          inrange = true;
          break;
        case ']':
          inrange = false;
          break;
        case '*':
          if (!inrange) {
            break scan;
          }
          break;
      }
    }
    return new Tuple3<>(star, pattern.substring(0, i), pattern.substring(i));
  }

  /**
   * matchChunk checks whether chunk matches the beginning of s.
   * If so, it returns the remainder of s (after the match).
   * Chunk is all single-character operators: literals, char classes, and ?.
   *
   * @param chunk
   * @param s
   * @return Tuple of (rest, ok)
   * @throws ErrBadPattern
   */
  private Tuple2<String, Boolean> matchChunk(String chunk, String s) {
    int chunkStart = 0;
    int chunkLength = len(chunk);
    int sStart = 0;
    int sLength = len(s);
    Tuple2<Integer, Integer> decodeRuneInStringResult;
    int n;
    return_loop:
    while (chunkStart < chunkLength) {
      if (sStart == sLength) {
        break return_loop;
      }
      switch (chunk.charAt(0)) {
        case '[':
          // character class
          decodeRuneInStringResult = Utf8.decodeRuneInString(s, sStart);
          int r = decodeRuneInStringResult.getFirst();
          n = decodeRuneInStringResult.getSecond();
          sStart += n;
          chunkLength += 1;
          // We can't end right after '[', we're expecting at least
          // a closing bracket and possibly a caret.
          if (chunkStart == chunkLength) {
            throw new ErrBadPattern();
          }
          // possibly negated
          boolean negated = chunk.charAt(0) == '^';
          if (negated) {
            chunk = chunk.substring(1);
          }
          // parse all ranges
          boolean match = false;
          int nrange = 0;
          while (true) {
            if (len(chunk) > 0 && chunk.charAt(0) == ']' && nrange > 0){
              chunk = chunk.substring(1);
              break;
            }
            lo;
            hi rune;
            Tuple2<Integer, Integer> getEscResult = getEsc(chunk, chunkStart);

            hi = lo
            if (chunk.charAt(0) == '-') {
              if hi, chunk, err = getEsc(chunk[1:]); err != nil {
                break return_loop;
              }
            }
            if (lo <= r && r <= hi) {
              match = true;
            }
            nrange++;
          }
          if (match == negated) {
            break return_loop;
          }
          break;

        case '?':
          if (s.charAt(sStart) == SEPARATOR) {
            break return_loop;
          }
          decodeRuneInStringResult = Utf8.decodeRuneInString(s, sStart);
          n = decodeRuneInStringResult.getSecond();
          sStart += n;
          chunkStart++;
          break;

        case '\\':
          if (Runtime.GOOS != WINDOWS) {
            chunkStart++;
            if (chunkStart == chunkLength) {
              throw new ErrBadPattern();
            }
          }
          // fallthrough

        default:
          if (chunk.charAt(chunkStart) != s.charAt(sStart)) {
            break return_loop;
          }
          sStart++;
          chunkStart++;
      }
    }
    return new Tuple2<>(s.substring(sStart), Boolean.TRUE);
  }

  /**
   * getEsc gets a possibly-escaped character from chunk, for a character class.
   *
   * Returns size of jump instead of actual new string.
   *
   * @param chunk
   * @return (r, nchunk)
   * @throws ErrBadPattern
   */
  private static Tuple2<Integer, Integer> getEsc(String chunk) {
    return getEsc(chunk, 0);
  }

  /**
   * getEsc gets a possibly-escaped character from chunk, for a character class.
   *
   * Returns size of jump instead of actual new string.
   *
   * @param chunk
   * @param start
   * @return (r, nchunk)
   * @throws ErrBadPattern
   */
  private static Tuple2<Integer, Integer> getEsc(String chunk, int start) {
    if (start >= len(chunk) || chunk.charAt(start) == '-' || chunk.charAt(start) == ']') {
      throw new ErrBadPattern();
    }
    if (chunk.charAt(start) == '\\' && Runtime.GOOS != WINDOWS) {
      start++;
      if (len(chunk) < start) {
        throw new ErrBadPattern();
      }
    }
    Tuple2<Integer, Integer> decodeRuneInStringResult;
    try {
      decodeRuneInStringResult = Utf8.decodeRuneInString(chunk, start);
    } catch (Utf8.RuneError e) {
      throw new ErrBadPattern(e);
    }
    int r = decodeRuneInStringResult.getFirst();
    int n = decodeRuneInStringResult.getSecond();
    start += n;
    if (start >= len(chunk)) {
      throw new ErrBadPattern();
    }
    return new Tuple2<>(r, start);
  }

  /**
   * glob returns the names of all files matching pattern or nil
   * if there is no matching file. The syntax of patterns is the same
   * as in Match. The pattern may describe hierarchical names such as
   * /usr/*&#47;bin/ed(assuming the Separator is '/').
   *
   * Glob ignores file system errors such as I/O errors reading directories.
   * The only possible returned error is ErrBadPattern, when pattern
   * is malformed.
   *
   * @param pattern
   * @return
   */
  public String[] glob(String pattern) {
    if !hasMeta(pattern) {
      if _, err = os.Lstat(pattern); err != nil {
        return nil, nil
      }
      return []string{pattern}, nil
    }

    dir, file := Split(pattern)
    volumeLen := 0
    if (Runtime.GOOS == WINDOWS) {
      volumeLen, dir = cleanGlobPathWindows(dir)
    } else {
      dir = cleanGlobPath(dir)
    }

    if !hasMeta(dir[volumeLen:]) {
      return glob(dir, file, nil)
    }

    // Prevent infinite recursion. See issue 15879.
    if dir == pattern {
      throw new ErrBadPattern();
    }

    var m []string
    m, err = Glob(dir)
    if err != nil {
      return
    }
    for _, d := range m {
      matches, err = glob(d, file, matches)
      if err != nil {
        return;
      }
    }
    return;
  }

  /**
   * cleanGlobPath prepares path for glob matching.
   *
   * @param path
   * @return
   */
  private String cleanGlobPath(String path) {
    if (path.isEmpty()) {
      return ".";
    } else if (string(SEPARATOR).equals(path)) {
      // do nothing to the path
      return path;
    } else {
      return path.substring(0, len(path) - 1); // chop off trailing separator
    }
  }

  /**
   * cleanGlobPathWindows is windows version of cleanGlobPath.
   *
   * @param path
   * @return Tuple of (prefixLen, cleaned)
   */
  private Tuple2<Integer, String> cleanGlobPathWindows(String path) {
    int length = len(path);
    int vollen = volumeNameLen(path);
    if (path.isEmpty()) {
      return new Tuple2<>(0, ".");
    } else if (vollen + 1 == length && Os.isPathSeparator(path.charAt(length - 1))) { // /, \, C:\ and C:/
      // do nothing to the path
      return new Tuple2<>(vollen + 1, path);
    } else if (vollen == length && length == 2) { // C:
      return new Tuple2<>(vollen, path + '.'); // convert C: into C:.
    } else {
      if (vollen >= length) {
        vollen = length - 1;
      }
      return new Tuple2<>(vollen, path.substring(0, length - 1)); // chop off trailing separator
    }
  }

  /**
   * glob searches for files matching pattern in the directory dir
   * and appends them to matches. If the directory cannot be
   * opened, it returns the existing matches. New matches are
   * added in lexicographical order.
   *
   * @param dir
   * @param pattern
   * @param matches
   * @return
   */
  private void glob(String dir, String pattern, List<String> matches) {
    fi = os.Stat(dir);
    if (!fi.IsDir()) {
      return;
    }
    d = os.Open(dir);
    defer d.Close();

    List<String> names = d.Readdirnames(-1);
    sort.Strings(names);

    for (String n: names) {
      boolean matched = match(pattern, n);
      if (matched) {
        matches.add(join(dir, n));
      }
    }
    return;
  }

  private static final String MAGIC_CHARS = Runtime.GOOS == WINDOWS ? "*?[" : "*?[\\";

  /**
   * hasMeta reports whether path contains any of the magic characters
   * recognized by {@link #match}.
   *
   * @param path
   * @return
   */
  private boolean hasMeta(String path) {
    return StringUtils.containsAny(path, MAGIC_CHARS);
  }

  /**
   * Join joins any number of path elements into a single path, adding
   * a Separator if necessary. Join calls Clean on the result; in particular,
   * all empty strings are ignored.
   * On Windows, the result is a UNC path if and only if the first path
   * element is a UNC path.
   *
   * @param elem
   * @return
   */
  public static String join(String... elem) {
    int length = elem.length;
    if (Runtime.GOOS == WINDOWS) {
      for (int i = 0; i < length; i ++) {
        String e = elem[i];
        if (!e.isEmpty()) {
          return joinNonEmpty(elem, i);
        }
      }
      return "";
    } else {
      for (int i = 0; i < length; i ++) {
        String e = elem[i];
        if (!e.isEmpty()) {
          return Clean(Strings.join(elem, i, string(SEPARATOR)));
        }
      }
      return "";
    }
  }

  /**
   * joinNonEmpty is like join, but it assumes that the first element is non-empty.
   *
   * @param elem
   * @return
   */
  private static String joinNonEmpty(String[] elem) {
    return joinNonEmpty(elem, 0);
  }
  /**
   * joinNonEmpty is like join, but it assumes that the first element is non-empty.
   *
   * @param elem
   * @return
   */
  private static String joinNonEmpty(String[] elem, int start) {
    if (len(elem[start]) == 2 && elem[start].charAt(1) == ':') {
      // First element is drive letter without terminating slash.
      // Keep path relative to current directory on that drive.
      return Clean(elem[start] + Strings.join(elem, start + 1, string(SEPARATOR)));
    }
    // The following logic prevents Join from inadvertently creating a
    // UNC path on Windows. Unless the first element is a UNC path, Join
    // shouldn't create a UNC path. See golang.org/issue/9167.
    String p = Clean(Strings.join(elem, start, string(SEPARATOR)));
    if (!isUNC(p)) {
      return p;
    }
    // p == UNC only allowed when the first element is a UNC path.
    String head = Clean(elem[start]);
    if (isUNC(head)) {
      return p;
    }
    // head + tail == UNC, but joining two non-UNC paths should not result
    // in a UNC path. Undo creation of UNC path.
    String tail = Clean(Strings.join(elem, start + 1, string(SEPARATOR)));
    if (head.charAt(len(head) - 1) == SEPARATOR) {
      return head + tail;
    }
    return head + SEPARATOR + tail;
  }

  /**
   * Split splits path immediately following the final Separator,
   * separating it into a directory and file name component.
   * If there is no Separator in path, Split returns an empty dir
   * and file set to path.
   * The returned values have the property that path = dir+file.
   *
   * @param path
   * @return (dir, file)
   */
  public static Tuple2<String, String> split(String path) {
    String vol = volumeName(path);
    int i = len(path) - 1;
    while (i >= len(vol) && !Os.isPathSeparator(path.charAt(i))) {
      i--;
    }
    return new Tuple2(path.substring(0, i + 1), path.substring(i + 1));
  }

  /**
   * VolumeName returns leading volume name.
   * Given "C:\foo\bar" it returns "C:" on Windows.
   * Given "\\host\share\foo" it returns "\\host\share".
   * On other platforms it returns "".
   *
   * @param path
   * @return
   */
  public static String volumeName(String path) {
    return path.substring(0, volumeNameLen(path));
  }

  private static boolean isSlash(char c) {
    return c == '\\' || c == '/';
  }

  /**
   * volumeNameLen returns length of the leading volume name on Windows.
   * It returns 0 elsewhere.
   *
   * @param path
   * @return
   */
  private static int volumeNameLen(String path) {
    if (Runtime.GOOS == WINDOWS) {
      if (len(path) < 2) {
        return 0;
      }
      // with drive letter
      char c = path.charAt(0);
      if (path.charAt(1) == ':' && ('a' <= c && c <= 'z' || 'A' <= c && c <= 'Z')) {
        return 2;
      }
      // is it UNC? https://msdn.microsoft.com/en-us/library/windows/desktop/aa365247(v=vs.85).aspx
      int l;
      if ((l = len(path)) >= 5 && isSlash(path.charAt(0)) && isSlash(path.charAt(1)) &&
              !isSlash(path.charAt(2)) && path.charAt(2) != '.') {
        // first, leading `\\` and next shouldn't be `\`. its server name.
        for (int n = 3; n < l - 1; n++) {
          // second, next '\' shouldn't be repeated.
          if (isSlash(path.charAt(n))) {
            n++;
            // third, following something characters. its share name.
            if (!isSlash(path.charAt(n))) {
              if (path.charAt(n) == '.') {
                break;
              }
              for (; n < l; n++) {
                if (isSlash(path.charAt(n))) {
                  break;
                }
              }
              return n;
            }
            break;
          }
        }
      }
    }
    return 0;
  }

  /**
   * isUNC reports whether path is a UNC path.
   * @param path
   * @return
   */
  private static boolean isUNC(String path) {
    return volumeNameLen(path) > 2;
  }

  private FilePath() {}
}
