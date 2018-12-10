package io.github.scambon.cliwrapper4j.converters;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public abstract class AbstractRegexResultConverter<Out> extends AbstractDelegatingOutputExtractingResultConverter<Out> {
	
	private final Pattern pattern;

	protected AbstractRegexResultConverter(IConverter<String, Out> delegate, String regex) {
		super(delegate);
		this.pattern = Pattern.compile(regex);
	}

	protected String extractRelevantOutput(String output) {
		Matcher matcher = pattern.matcher(output);
		if(matcher.find()) {
			return matcher.group(1);
		}
		throw new IllegalArgumentException("Pattern '"+pattern+"' did not match output '"+output+"'");
	}
}