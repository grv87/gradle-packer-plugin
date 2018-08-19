package org.fidata.gradle.packer.template.annotations

import com.fasterxml.jackson.annotation.JsonUnwrapped
import org.gradle.api.tasks.Nested
import groovy.transform.AnnotationCollector

@JsonUnwrapped
@Nested
@AnnotationCollector
@interface Inline {
}
