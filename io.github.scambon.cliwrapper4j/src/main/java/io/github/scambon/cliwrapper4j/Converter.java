package io.github.scambon.cliwrapper4j;

import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import io.github.scambon.cliwrapper4j.converters.IConverter;
import io.github.scambon.cliwrapper4j.converters.StringQuotedIfNeededConverter;

@Retention(RUNTIME)
@Target(PARAMETER)
public @interface Converter {
	Class<? extends IConverter<?, String>> value() default StringQuotedIfNeededConverter.class;
}