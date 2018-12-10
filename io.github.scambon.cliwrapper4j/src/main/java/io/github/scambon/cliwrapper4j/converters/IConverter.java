package io.github.scambon.cliwrapper4j.converters;

public interface IConverter<In, Out> {
	boolean canConvert(Class<In> inClass, Class<Out> outClass);
	Out convert(In in, Class<Out> outClass);
}
