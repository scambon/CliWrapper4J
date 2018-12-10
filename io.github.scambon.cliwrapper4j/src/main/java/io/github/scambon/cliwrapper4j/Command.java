package io.github.scambon.cliwrapper4j;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import io.github.scambon.cliwrapper4j.converters.IConverter;
import io.github.scambon.cliwrapper4j.converters.Result;
import io.github.scambon.cliwrapper4j.converters.ResultConverter;

@Retention(RUNTIME)
@Target(METHOD)
public @interface Command {
	String value();
	Class<? extends IConverter<Result, ?>> converter() default ResultConverter.class;
	Class<?> outType() default Object.class;
	boolean validateReturnCode() default true;
	int[] expectedReturnCodes() default 0;
}