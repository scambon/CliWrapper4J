package io.github.scambon.cliwrapper4j.converters;

public interface IConvertlet<In, Out> {
	Out convert(In in);
}