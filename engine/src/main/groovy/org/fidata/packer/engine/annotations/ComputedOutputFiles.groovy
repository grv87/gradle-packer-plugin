package org.fidata.packer.engine.annotations

import com.fasterxml.jackson.annotation.JsonIgnore
import groovy.transform.AnnotationCollector
import groovy.transform.CompileStatic
import groovy.transform.Internal
import org.gradle.api.tasks.OutputFiles

@AnnotationCollector([JsonIgnore, OutputFiles, Internal])
@CompileStatic
@interface ComputedOutputFiles {
}
