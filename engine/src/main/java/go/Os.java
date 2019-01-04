package go;

import org.fidata.CustomEnumSet32;

import java.nio.file.attribute.FileTime;
import java.util.EnumSet;
import java.util.Set;

import static go.Runtime.GoOS.*;

public final class Os {
  public static final char PATH_SEPARATOR = Runtime.GOOS == WINDOWS ? '\\' : '/';

  /**
   * IsPathSeparator reports whether c is a directory separator character.
   * @param c
   * @return
   */
  public static boolean isPathSeparator(char c) {
    // NOTE: Windows accept / as path separator.
    return Runtime.GOOS == WINDOWS ? c == '\\' || c == '/' : PATH_SEPARATOR == c;
  }

  public enum FileModeEnum implements value {
    /**
     * d: is a directory
     */
    ModeDir(1 << (32 - 1 - 0)),
    /**
     * a: append-only
     */
    ModeAppend(1 << (32 - 1 - 1)),
    /**
     * l: exclusive use
     */
    ModeExclusive(1 << (32 - 1 - 2)),
    /**
     * T: temporary file; Plan 9 only
     */
    ModeTemporary(1 << (32 - 1 - 3)),
    /**
     * L: symbolic link
     */
    ModeSymlink(1 << (32 - 1 - 4)),
    /**
     * D: device file
     */
    ModeDevice(1 << (32 - 1 - 5)),
    /**
     * p: named pipe (FIFO)
     */
    ModeNamedPipe(1 << (32 - 1 - 6)),
    /**
     * S: Unix domain socket
     */
    ModeSocket(1 << (32 - 1 - 7)),
    /**
     * u: setuid
     */
    ModeSetuid(1 << (32 - 1 - 8)),
    /**
     * g: setgid
     */
    ModeSetgid(1 << (32 - 1 - 9)),
    /**
     * c: Unix character device, when ModeDevice is set
     */
    ModeCharDevice(1 << (32 - 1 - 10)),
    /**
     * t: sticky
     */
    ModeSticky(1 << (32 - 1 - 11)),
    /**
     * ?: non-regular file; nothing else is known about this file
     */
    ModeIrregular(1 << (32 - 1 - 12));

    public final int value;

    FileModeEnum(int value) {
      this.value = value;
    }

    // Mask for the type bits. For regular files, none will be set.
    static final int ModeType = ModeDir.value | ModeSymlink.value | ModeNamedPipe.value | ModeSocket.value | ModeDevice.value | ModeIrregular.value;

    static final int /*FileMode*/ modePerm = 0777; // Unix permission bits
  }

  public class FileMode extends CustomEnumSet32<FileModeEnum> {
    FileMode() {
      super(FileModeEnum, )
    }

  }

  /**
   * A FileInfo describes a file and is returned by Stat and Lstat.
   */
  public interface FileInfo {
    /**
     * Base name of the file
     *
     * @return
     */
    String getName();

    /**
     * Length in bytes for regular files; system-dependent for others
     *
     * @return
     */
    long getSize();

    /**
     * File mode bits
     *
     * @return
     */
    FileMode getMode();

    /**
     * Modification time
     *
     * @return
     */
    FileTime getModTime();

    /**
     * An abbreviation for getMode().isDir()
     *
     * @return
     */
    boolean isDir();
  }

  private Os() {}
}
