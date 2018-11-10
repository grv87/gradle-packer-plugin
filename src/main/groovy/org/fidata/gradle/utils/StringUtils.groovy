package org.fidata.gradle.utils

import groovy.transform.CompileStatic

@CompileStatic
class StringUtils {
  static final Map<String, String> stringize(Map<? extends Object, ? extends Object> stringyThings) {
    (Map<String, String>)stringyThings.collectEntries { Map.Entry<? extends Object, ? extends Object> entry -> [(org.ysb33r.grolifant.api.StringUtils.stringize(entry.key)): org.ysb33r.grolifant.api.StringUtils.stringize(entry.value)] }
  }
}
