package io.github.scambon.cliwrapper4j.aggregators;

import java.util.Arrays;
import java.util.stream.Collectors;

public final class SymbolAggregator implements IAgregator {

	@Override
	public String aggregate(String commandOrOption, String value, String separator) {
		return Arrays.asList(commandOrOption, value)
			.stream()
			.filter(element -> element!=null && !element.isEmpty())
			.collect(Collectors.joining(separator));
	}
}
