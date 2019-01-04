package go;

import org.apache.commons.lang3.SystemUtils;

public final class Runtime {
  public enum GoOS {
    DARWIN,
    DRAGONFLY,
    JS, // WebAssembly
    LINUX,
    ANDROID,
    SOLARIS,
    FREEBSD,
    NACL, // Native Client
    NETBSD,
    OPENBSD,
    PLAN9,
    WINDOWS,
    ZOS

    // @JsonValue
    @Override
    public String toString() {
      return this.name().toLowerCase();
    }
  }

  // @Lazy
  public static final GoOS GOOS;

  static {
    if (SystemUtils.IS_OS_MAC) {
      GOOS = GoOS.DARWIN;
    } else if (SystemUtils.IS_OS_LINUX) {
      GOOS = GoOS.LINUX;
    } else if (SystemUtils.IS_OS_SOLARIS) {
      GOOS = GoOS.SOLARIS;
    } else if (SystemUtils.IS_OS_FREE_BSD) {
      GOOS = GoOS.FREEBSD;
    } else if (SystemUtils.IS_OS_NET_BSD) {
      GOOS = GoOS.NETBSD;
    } else if (SystemUtils.IS_OS_OPEN_BSD) {
      GOOS = GoOS.OPENBSD;
    } else if (SystemUtils.IS_OS_WINDOWS) {
      GOOS = GoOS.WINDOWS;
    } else if (SystemUtils.IS_OS_ZOS) {
      GOOS = GoOS.ZOS;
    } else {
      // JS and NACL will never be returned since JVM can't run inside these environments
      // TODO: No way to detect DragonFly, Android, Plan9
      throw new UnsupportedOperationException("Unknown operating system");
    }
  }

  private Runtime() {}
}
