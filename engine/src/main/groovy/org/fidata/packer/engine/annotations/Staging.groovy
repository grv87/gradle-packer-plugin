package org.fidata.packer.engine.annotations

import groovy.transform.AnnotationCollector
import groovy.transform.CompileStatic
import org.gradle.api.tasks.Internal
import java.lang.annotation.Documented

/**
 * This annotation is used to indicate that the property specifies
 * temporary paths or file names on remote or host systems,
 * and so is ignored for up-to-date detection
 */
@AnnotationCollector([Internal])
@Documented
@CompileStatic
@interface Staging {
}
