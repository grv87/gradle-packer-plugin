package com.github.hashicorp.packer.engine.annotations

import com.fasterxml.jackson.annotation.JsonUnwrapped
import org.gradle.api.tasks.Nested
import groovy.transform.AnnotationCollector

@JsonUnwrapped
@Nested
@AnnotationCollector
@interface Inline {
}
