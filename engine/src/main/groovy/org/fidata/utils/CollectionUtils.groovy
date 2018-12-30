package org.fidata.utils

import groovy.transform.CompileStatic
import org.gradle.api.provider.Provider
import java.util.concurrent.Callable
import java.util.function.Supplier

@CompileStatic
final class CollectionUtils {
  /**
   *
   * @param value
   * @return
   * @throws TODO
   */
  static Serializable flattenValue(Object value) {
    if (Callable.isInstance(value)) {
      flattenValue(((Callable)value).call())
    } else if (Provider.isInstance(value)) {
      flattenValue(((Provider)value).get())
    } else if (Supplier.isInstance(value)) {
      flattenValue(((Supplier)value).get())
    } else {
      // If it is not Serializable then we get cast error here - it's what expected
      (Serializable)value
    }
  }

  private CollectionUtils() {}
}
