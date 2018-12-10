package io.github.scambon.cliwrapper4j.converters;

import java.util.Arrays;
import java.util.List;

public class CompositeConverter<In, Out> implements IConverter<In, Out>{

	private final List<IConverter<In, Out>> converters;

	public CompositeConverter(List<IConverter<In, Out>> converters) {
		this.converters = converters;
	}
	
	public CompositeConverter(IConverter<In, Out>... converters) {
		this(Arrays.asList(converters));
	}

	@Override
	public boolean canConvert(Class<In> inClass, Class<Out> outClass) {
		return converters.stream()
			.anyMatch(converter ->
				converter.canConvert(inClass, outClass));
	}

	@Override
	public Out convert(In in, Class<Out> outClass) {
		Class<In> inClass = (Class<In>) in.getClass();
		return converters.stream()
			.filter(converter -> converter.canConvert(inClass, outClass))
			.map(converter -> converter.convert(in, outClass))
			.findFirst()
			.orElseThrow(() -> new IllegalArgumentException("Could not convert '"+in+"' to '"+outClass+"'"));
	}
}