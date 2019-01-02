package org.fidata.packer.engine.annotations

import com.fasterxml.jackson.annotation.JsonIgnore
import groovy.transform.AnnotationCollector
import groovy.transform.CompileStatic
import groovy.transform.Internal

import java.lang.annotation.Documented

@AnnotationCollector([JsonIgnore, ExtraProcessed, Internal])
@Documented
@CompileStatic
@interface ComputedExtraProcessed {
}
