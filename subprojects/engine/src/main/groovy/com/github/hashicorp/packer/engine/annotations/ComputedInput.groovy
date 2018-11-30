package com.github.hashicorp.packer.engine.annotations

import com.fasterxml.jackson.annotation.JsonIgnore
import groovy.transform.AnnotationCollector
import org.gradle.api.tasks.Input

@JsonIgnore
@Input
@AnnotationCollector
@interface ComputedInput {
}