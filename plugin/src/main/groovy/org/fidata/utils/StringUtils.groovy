package org.fidata.utils

import static org.ysb33r.grolifant.api.StringUtils.stringize
import groovy.transform.CompileStatic

@CompileStatic
class StringUtils {
  static final Map<String, String> stringize(Map<? extends Object, ? extends Object> stringyThings) {
    (Map<String, String>)stringyThings.collectEntries { Object key, Object value ->
      [(stringize(key)): stringize(value)]
    }
  }
}
