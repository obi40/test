package com.optimiza.core.common.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * InterceptorFree.java
 * 
 * Used in ServiceInterceptor to exclude CLASSES AND METHODS from AOP.
 * 
 * @author Abdullah Imran <aImran@optimizasolutions.com>
 * @since Nov/07/2018
 **/
@Target({ ElementType.TYPE, ElementType.METHOD })
@Retention(RetentionPolicy.RUNTIME)
public @interface InterceptorFree {
}