package com.github.hashicorp.packer.engine.annotations

import com.fasterxml.jackson.annotation.JsonIgnore
import groovy.transform.AnnotationCollector
import groovy.transform.CompileStatic
import org.gradle.api.tasks.InputFile

@AnnotationCollector([JsonIgnore, InputFile])
@CompileStatic
@interface ComputedInputFile {
}
