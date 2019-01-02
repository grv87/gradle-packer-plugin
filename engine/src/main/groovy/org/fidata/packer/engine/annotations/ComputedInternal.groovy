package org.fidata.packer.engine.annotations

import com.fasterxml.jackson.annotation.JsonIgnore
import groovy.transform.AnnotationCollector
import groovy.transform.CompileStatic
import org.gradle.api.tasks.Internal

@AnnotationCollector([JsonIgnore, Internal, groovy.transform.Internal])
@CompileStatic
@interface ComputedInternal {
}
