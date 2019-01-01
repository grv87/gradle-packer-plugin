package go.runtime

import com.fasterxml.jackson.annotation.JsonValue
import org.apache.commons.lang3.SystemUtils

enum GOOS {
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
  ZOS;

  @JsonValue
  @Override
  String toString() {
    this.name().toLowerCase()
  }

  @Lazy
  static final GOOS CURRENT = {
    switch (Boolean.TRUE) {
     case SystemUtils.IS_OS_MAC:
      return DARWIN
     case SystemUtils.IS_OS_LINUX:
      return LINUX
    case SystemUtils.IS_OS_SOLARIS:
      return SOLARIS
    case SystemUtils.IS_OS_FREE_BSD:
      return FREEBSD
    case SystemUtils.IS_OS_NET_BSD:
      return NETBSD
    case SystemUtils.IS_OS_OPEN_BSD:
      return OPENBSD
    case SystemUtils.IS_OS_WINDOWS:
      return WINDOWS
    case SystemUtils.IS_OS_ZOS:
      return ZOS
    default:
      // JS and NACL will never be returned since JVM can't run inside these environments
      // TODO: No way to detect DragonFly, Android, Plan9
      throw new UnsupportedOperationException('Unknown operating system')
    }
  }()

}
