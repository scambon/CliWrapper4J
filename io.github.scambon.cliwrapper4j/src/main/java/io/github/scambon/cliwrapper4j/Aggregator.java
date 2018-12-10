package io.github.scambon.cliwrapper4j;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import io.github.scambon.cliwrapper4j.aggregators.IAgregator;
import io.github.scambon.cliwrapper4j.aggregators.SymbolAggregator;

@Retention(RUNTIME)
@Target(METHOD)
public @interface Aggregator {
	String value() default " ";
	Class<? extends IAgregator> aggregator() default SymbolAggregator.class; 
}