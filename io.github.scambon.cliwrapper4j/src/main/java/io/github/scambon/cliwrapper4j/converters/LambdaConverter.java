package io.github.scambon.cliwrapper4j.converters;

public class LambdaConverter<In, Out> implements IConverter<In, Out>{
	
	private final Class<In> inClass;
	private final Class<Out> outClass;
	private final IConvertlet<In, Out> convertlet;
	
	public LambdaConverter(Class<In> inClass, Class<Out> outClass, IConvertlet<In, Out> convertlet) {
		this.inClass = inClass;
		this.outClass = outClass;
		this.convertlet = convertlet;
	}

	@Override
	public final boolean canConvert(Class<In> inClass, Class<Out> outClass) {
		return this.inClass.equals(inClass) && this.outClass.equals(outClass);
	}

	@Override
	public final Out convert(In in, Class<Out> outClass) {
		return convertlet.convert(in);
	}
}