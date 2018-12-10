package io.github.scambon.cliwrapper4j.flatteners;

import java.util.List;

public final class JoiningOnDelimiterFlattener implements IFlattener {
	
	public final String flatten(List<String> parameters, String delimiter) {
		return String.join(delimiter, parameters);
	}
}