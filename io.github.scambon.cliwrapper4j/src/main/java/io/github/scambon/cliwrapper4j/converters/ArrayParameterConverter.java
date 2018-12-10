package io.github.scambon.cliwrapper4j.converters;

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import io.github.scambon.cliwrapper4j.flatteners.IFlattener;

public final class ArrayParameterConverter<In> implements IConverter<In[], String>{
	
	private final Class<In> elementClass;
	private final IConverter<In, String> elementConverter;
	private final IFlattener flattener;
	private final String flattenerParameter;

	public ArrayParameterConverter(Class<In> elementClass, IConverter<In, String> elementConverter,
			IFlattener flattener, String flattenerParameter) {
		this.elementClass = elementClass;
		this.elementConverter = elementConverter;
		this.flattener = flattener;
		this.flattenerParameter = flattenerParameter;
	}

	@Override
	@SuppressWarnings("unchecked")
	public boolean canConvert(Class<In[]> inClass, Class<String> outClass) {
		Class<In[]> arrayClass = (Class<In[]>) Array.newInstance(elementClass, 0).getClass();
		return arrayClass.isAssignableFrom(inClass) && elementConverter.canConvert(elementClass, outClass);
	}

	@Override
	public String convert(In[] in, Class<String> outClass) {
		List<String> convertedValues = Arrays.stream(in)
			.map(element -> elementConverter.convert(element, outClass))
			.collect(Collectors.toList());
		String convertedIterable = flattener.flatten(convertedValues, flattenerParameter);
		return convertedIterable;
	}
}