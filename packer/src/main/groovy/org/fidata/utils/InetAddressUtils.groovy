package org.fidata.utils

import groovy.transform.CompileStatic

@CompileStatic
final class InetAddressUtils {
  static isLocalHost(String url) {
    InetAddress address = InetAddress.getByName(new URL(url).host)
    if (address.anyLocalAddress || address.loopbackAddress) {
      return true
    }

    // Check if the address is defined on any interface
    try {
      return NetworkInterface.getByInetAddress(address) != null
    } catch (SocketException e) {
      return false
    }
  }

  private InetAddressUtils() {}
}
