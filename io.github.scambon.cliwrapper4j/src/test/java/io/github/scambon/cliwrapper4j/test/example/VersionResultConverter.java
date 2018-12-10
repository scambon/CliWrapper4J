package io.github.scambon.cliwrapper4j.test.example;

import java.lang.Runtime.Version;

import io.github.scambon.cliwrapper4j.converters.AbstractRegexResultConverter;
import io.github.scambon.cliwrapper4j.converters.IConverter;
import io.github.scambon.cliwrapper4j.converters.LambdaConverter;

public final class VersionResultConverter extends AbstractRegexResultConverter<Version> {
	
	private static final IConverter<String, Version> CONVERTER = new LambdaConverter<>(
		String.class,
		Version.class,
		output -> Version.parse(output));

	public VersionResultConverter() {
		super(CONVERTER, "java (\\d+(\\.\\d+)*)");
	}
}