package org.fidata.packer.engine.annotations

import groovy.transform.AnnotationCollector
import groovy.transform.CompileStatic
import org.gradle.api.tasks.Internal

/**
 * This annotation is used to indicate that interpolated value of the property
 * requires extra procession and is exposed to Gradle up-to-date detection
 * in other ways
 */
@AnnotationCollector([Internal])
@CompileStatic
@interface ExtraProcessed {
}
