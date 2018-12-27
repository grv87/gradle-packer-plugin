package org.fidata.packer.engine.annotations

import com.fasterxml.jackson.annotation.JsonUnwrapped
import groovy.transform.CompileStatic
import org.gradle.api.tasks.Nested
import groovy.transform.AnnotationCollector

@AnnotationCollector([JsonUnwrapped, Nested])
@CompileStatic
@interface Inline {
}
