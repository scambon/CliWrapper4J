package io.github.scambon.cliwrapper4j.converters;

public abstract class AbstractDelegatingOutputExtractingResultConverter<Out> implements IConverter<Result, Out> {
	
	private final IConverter<String, Out> delegate;
	
	protected AbstractDelegatingOutputExtractingResultConverter(IConverter<String, Out> delegate) {
		this.delegate = delegate;
	}

	@Override
	public final boolean canConvert(Class<Result> inClass, Class<Out> outClass) {
		return Result.class.equals(inClass) && delegate.canConvert(String.class, outClass);
	}
	
	@Override
	public final Out convert(Result in, Class<Out> outClass) {
		String output = in.getOutput();
		String relevantString = extractRelevantOutput(output);
		Out convertedValue = delegate.convert(relevantString, outClass);
		return convertedValue;
	}

	protected abstract String extractRelevantOutput(String output);
}