package go.runtime

import com.fasterxml.jackson.annotation.JsonValue
import org.apache.commons.lang3.SystemUtils

enum GOOS {
  DARWIN,
  DRAGONFLY,
  JS,
  LINUX,
  ANDROID,
  SOLARIS,
  FREEBSD,
  NACL,
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

  static GOOS current() {
    if (SystemUtils.IS_OS_MAC) {
      return DARWIN
    } else if (SystemUtils.IS_OS_LINUX) {
      return LINUX
    } else if (SystemUtils.IS_OS_SOLARIS) {
      return SOLARIS
    } else if (SystemUtils.IS_OS_FREE_BSD) {
      return FREEBSD
    } else if (SystemUtils.IS_OS_NET_BSD) {
      return NETBSD
    } else if (SystemUtils.IS_OS_OPEN_BSD) {
      return OPENBSD
    } else if (SystemUtils.IS_OS_WINDOWS) {
      return WINDOWS
    } else if (SystemUtils.IS_OS_ZOS) {
      return ZOS
    } else {
      // JS and NACL will never be returned since JVM can't run inside these environments
      // TODO: No way to detect DragonFly, Android, Plan9
      throw new UnsupportedOperationException()
    }
  }

}
