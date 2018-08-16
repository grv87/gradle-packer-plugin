package org.fidata.gradle.packer.template.annotations

import com.fasterxml.jackson.annotation.JsonUnwrapped
import groovy.transform.AnnotationCollector
import org.gradle.api.tasks.Nested

@JsonUnwrapped
@Nested
@AnnotationCollector
@interface Inline {
}
