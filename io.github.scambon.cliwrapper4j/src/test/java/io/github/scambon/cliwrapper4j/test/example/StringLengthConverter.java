package io.github.scambon.cliwrapper4j.test.example;

import java.util.Objects;

import io.github.scambon.cliwrapper4j.converters.LambdaConverter;

public class StringLengthConverter extends LambdaConverter<Object, String>{

	public StringLengthConverter() {
		super(Object.class, String.class, object -> ""+Objects.toString(object).length());
	}
}