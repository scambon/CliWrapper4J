package io.github.scambon.cliwrapper4j;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import io.github.scambon.cliwrapper4j.flatteners.IFlattener;
import io.github.scambon.cliwrapper4j.flatteners.JoiningOnDelimiterFlattener;

@Retention(RUNTIME)
@Target(METHOD)
public @interface Flattener {
	String value() default " ";
	Class<? extends IFlattener> flattener() default JoiningOnDelimiterFlattener.class; 
}