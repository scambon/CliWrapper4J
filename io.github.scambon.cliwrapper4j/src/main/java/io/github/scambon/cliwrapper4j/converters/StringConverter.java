package io.github.scambon.cliwrapper4j.converters;

import java.util.Objects;

public class StringConverter implements IConverter<Object, String>{

	@Override
	public boolean canConvert(Class<Object> inClass, Class<String> outClass) {
		return String.class.equals(outClass);
	}

	@Override
	public String convert(Object in, Class<String> outClass) {
		return Objects.toString(in);
	}
}