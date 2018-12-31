package org.fidata.packer.engine.annotations

import groovy.transform.AnnotationCollector
import groovy.transform.CompileStatic
import org.gradle.api.tasks.Internal

/**
 * This annotation is used to indicate that the property
 * affects configuration of VM/instance launched by builder
 * and doesn't affect resulted artifact,
 * and so is ignored for up-to-date detection
 */
@AnnotationCollector([Internal])
@CompileStatic
@interface LaunchedVMConfiguration {
}
