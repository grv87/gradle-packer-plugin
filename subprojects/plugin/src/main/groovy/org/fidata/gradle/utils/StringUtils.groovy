package org.fidata.gradle.utils

import groovy.transform.CompileStatic

@CompileStatic
class StringUtils {
  static final Map<String, String> stringize(Map<? extends Object, ? extends Object> stringyThings) {
    (Map<String, String>)stringyThings.collectEntries { Object key, Object value ->
      [(org.ysb33r.grolifant.api.StringUtils.stringize(key)): org.ysb33r.grolifant.api.StringUtils.stringize(value)]
    }
  }
}
