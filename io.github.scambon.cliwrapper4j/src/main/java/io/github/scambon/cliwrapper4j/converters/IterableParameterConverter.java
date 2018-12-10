package io.github.scambon.cliwrapper4j.converters;

import java.util.List;
import java.util.Spliterator;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import io.github.scambon.cliwrapper4j.flatteners.IFlattener;

public final class IterableParameterConverter<In> implements IConverter<Iterable<In>, String>{
	
	private final Class<In> elementClass;
	private final IConverter<In, String> elementConverter;
	private final IFlattener flattener;
	private final String flattenerParameter;

	public IterableParameterConverter(Class<In> elementClass, IConverter<In, String> elementConverter,
			IFlattener flattener, String flattenerParameter) {
		this.elementClass = elementClass;
		this.elementConverter = elementConverter;
		this.flattener = flattener;
		this.flattenerParameter = flattenerParameter;
	}

	@Override
	public boolean canConvert(Class<Iterable<In>> inClass, Class<String> outClass) {
		return Iterable.class.isAssignableFrom(inClass) && elementConverter.canConvert(elementClass, outClass);
	}

	@Override
	public String convert(Iterable<In> in, Class<String> outClass) {
		Spliterator<In> spliterator = in.spliterator();
		List<String> convertedValues = StreamSupport.stream(spliterator, false)
			.map(element -> elementConverter.convert(element, outClass))
			.collect(Collectors.toList());
		String convertedIterable = flattener.flatten(convertedValues, flattenerParameter);
		return convertedIterable;
	}
}