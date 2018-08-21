package org.fidata.gradle.packer.template.annotations

import java.lang.annotation.ElementType
import java.lang.annotation.Retention
import java.lang.annotation.RetentionPolicy
import java.lang.annotation.Target
import org.codehaus.groovy.transform.GroovyASTTransformationClass

@Retention(RetentionPolicy.RUNTIME) // TODO: SOURCE
@Target([ElementType.FIELD])
@GroovyASTTransformationClass(classes = [DefaultTransformation])
@interface Default {
  String value()
}
