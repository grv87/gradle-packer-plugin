package com.github.hashicorp.packer.engine.annotations

import com.github.hashicorp.packer.engine.ast.AutoImplementASTTransformation
import groovy.transform.CompileStatic
import org.codehaus.groovy.transform.GroovyASTTransformationClass
import java.lang.annotation.ElementType
import java.lang.annotation.Retention
import java.lang.annotation.RetentionPolicy
import java.lang.annotation.Target

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.SOURCE)
@GroovyASTTransformationClass(classes = [AutoImplementASTTransformation])
@CompileStatic
@interface AutoImplement {
}
