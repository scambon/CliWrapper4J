package io.github.scambon.cliwrapper4j.converters;

import io.github.scambon.cliwrapper4j.flatteners.IFlattener;

public class MultipleParameterConverter extends CompositeConverter<Object, String> {

	public <T> MultipleParameterConverter(
		Class<T> elementClass, IConverter<T, String> elementConverter, IFlattener flattener, String flattenerParameter) {
		super(
			new ArrayParameterConverter(elementClass, elementConverter, flattener, flattenerParameter),
			new IterableParameterConverter(elementClass, elementConverter, flattener, flattenerParameter));
	}
}