package com.github.hashicorp.packer.engine.annotations

import groovy.transform.CompileStatic

import java.lang.annotation.Documented
import java.lang.annotation.ElementType
import java.lang.annotation.Retention
import java.lang.annotation.RetentionPolicy
import java.lang.annotation.Target

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.SOURCE)
@Documented
@CompileStatic
@interface IgnoreIf {
  Class<? extends Closure<Boolean>> value()
}
