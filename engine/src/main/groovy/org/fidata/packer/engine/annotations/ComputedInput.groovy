package org.fidata.packer.engine.annotations

import com.fasterxml.jackson.annotation.JsonIgnore
import groovy.transform.AnnotationCollector
import groovy.transform.CompileStatic
import groovy.transform.Internal
import org.gradle.api.tasks.Input

@AnnotationCollector([JsonIgnore, Input, Internal])
@CompileStatic
@interface ComputedInput {
}
