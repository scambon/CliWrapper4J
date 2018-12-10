package io.github.scambon.cliwrapper4j.converters;

public class StringQuotedIfNeededConverter extends StringConverter {
	
	@Override
	public String convert(Object in, Class<String> outClass) {
		String convertedValue = super.convert(in, outClass);
		if(convertedValue.contains(" ")) {
			convertedValue = '"' + convertedValue + '"';
		}
		return convertedValue;
	}
}